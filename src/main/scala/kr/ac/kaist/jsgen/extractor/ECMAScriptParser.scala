package kr.ac.kaist.jsgen.extractor

import kr.ac.kaist.jsgen.{ error => _, _ }
import kr.ac.kaist.jsgen.ir.{ Id, UnitWalker }
import kr.ac.kaist.jsgen.extractor.algorithm.{ AlgoParser, HeadParser }
import kr.ac.kaist.jsgen.extractor.grammar.GrammarParser
import kr.ac.kaist.jsgen.spec._
import kr.ac.kaist.jsgen.spec.JsonProtocol._
import kr.ac.kaist.jsgen.spec.algorithm._
import kr.ac.kaist.jsgen.spec.grammar._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util.Useful._
import org.jsoup._
import org.jsoup.nodes._
import scala.collection.mutable.Stack

object ECMAScriptParser {
  def apply(
    version: String,
    query: String,
    detail: Boolean
  ): ECMAScript =
    apply(version, preprocess(version), query, detail)
  def apply(
    version: String,
    input: (Array[String], Document, Region),
    query: String,
    detail: Boolean
  ): ECMAScript = {
    implicit val (lines, document, region) = input

    // parse grammar
    implicit val grammar = parseGrammar

    // parse algorithm
    val algos = removeDup((
      if (query == "") parseAlgo(version, document, detail)
      else getElems(document, query).toList.flatMap(parseAlgo(version, _, detail))
    ) ++ manualAlgos(version))

    // parse intrincis
    val intrinsics = parseIntrinsic

    // special names
    var symbols = getSymbols(algos) ++ parseSymbol

    // aoids
    val aoids = parseAoids

    // section hierarchy
    val section = parseSection

    ECMAScript(version, grammar, algos, intrinsics, symbols, aoids, section)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // helper
  ////////////////////////////////////////////////////////////////////////////////
  // preprocess for spec.html
  def preprocess(version: String = VERSION): (Array[String], Document, Region) = {
    val cur = currentVersion(ECMA262_DIR)
    val src = if (cur == version) readFile(SPEC_HTML) else {
      changeVersion(version, ECMA262_DIR)
      val src = readFile(SPEC_HTML)
      changeVersion(cur, ECMA262_DIR)
      src
    }
    val lines = unescapeHtml(src).split(LINE_SEP)
    val cutted = dropNoScope(attachLines(src.split(LINE_SEP))).mkString(LINE_SEP)
    val document = Jsoup.parse(cutted)
    val region = Region(document)
    (lines, document, region)
  }

  // attach line numbers
  val startPattern = """(\s*<)([-a-z]+)([^<>]*(<[^<>]*>[^<>]*)*>[^<>]*)""".r
  val endPattern = """\s*</([-a-z]+)>\s*""".r
  val pairPattern = """(\s*<)([-a-z]+)([^<>]*(<[^<>]*>[^<>]*)*>.*</[-a-z]+>\s*)""".r
  val ignoreTags = Set("meta", "link", "style", "br", "img", "li", "p")
  def attachLines(lines: Array[String]): Array[String] = {
    val tagStack = Stack[(String, Int)]()
    var rngs = Map[Int, Int]()
    lines.zipWithIndex.foreach {
      case (line, k) => line match {
        case startPattern(_, tag, _, _) if !ignoreTags.contains(tag) =>
          tagStack.push((tag, k))
        case endPattern(tag) if !ignoreTags.contains(tag) =>
          var (expected, start) = tagStack.pop
          while (expected != tag) {
            val (e, s) = tagStack.pop
            expected = e
            start = s
          }
          rngs += start -> (k + 1)
        case _ =>
      }
    }
    lines.zipWithIndex.map(_ match {
      case (line @ startPattern(pre, tag, post, _), start) if !ignoreTags.contains(tag) =>
        rngs.get(start).fold(line)(end => s"$pre$tag s=$start e=$end$post")
      case (line @ pairPattern(pre, tag, post, _), start) if !ignoreTags.contains(tag) =>
        s"$pre$tag s=$start e=${start + 1}$post"
      case (line, _) => line
    })
  }

  // drop lines not in the scope of extraction
  val startLinePattern = "<emu-clause.*sec-ecmascript-data-types-and-values.*>".r
  val endLinePattern = "<emu-annex.*annexB.*>".r
  def dropNoScope(lines: Array[String]): Array[String] = {
    val startLineNum = lines.indexWhere(startLinePattern matches _)
    val endLineNum = lines.indexWhere(endLinePattern matches _)
    if (startLineNum == -1) error("[ECMAScript.dropAppendix] not found start line.")
    if (endLineNum == -1) error("[ECMAScript.dropAppendix] not found end line.")
    lines.slice(startLineNum, endLineNum)
  }

  // parse table#id > tag
  private def parseTable(
    query: String
  )(implicit document: Document): Array[Array[Element]] = {
    getElems(document, s"$query table > tbody > tr")
      .map(row => toArray(row.children))
  }

  private def getTargetElems(
    target: Element,
    detail: Boolean
  )(
    implicit
    lines: Array[String],
    document: Document
  ): Array[Element] = {
    // HTML elements with `emu-alg` tags
    // `emu-alg` that reside inside `emu-note` should be filtered out
    val emuAlgs = getElems(target, "emu-alg").filter(elem => {
      val isEmuNote = elem.parent().tagName() == "emu-note"
      val prev = elem.previousElementSibling()
      val isShortHand =
        if (prev != null) prev.text() == "Algorithm steps that say"
        else false
      !(isEmuNote || isShortHand)
    })

    // HTML elements for Early Error
    val earlyErrors = for {
      parentElem <- getElems(target, "emu-clause[id$=early-errors]")
      elem <- getElems(parentElem, "ul")
    } yield elem

    // HTML elements for table algorithms, with "Argument Type"
    val typeTableAlgs = getElems(target, "emu-table:contains(Argument Type)")

    // HTML elements with `emu-eqn` tags
    val emuEqns = getElems(target, "emu-eqn[aoid]")

    // target elements
    val elems = emuAlgs ++ earlyErrors ++ typeTableAlgs ++ emuEqns

    if (detail) {
      println(s"# algorithm elements: ${elems.size}")
      println(s"  - <emu-alg>: ${emuAlgs.size}")
      println(s"  - Early Error: ${earlyErrors.size}")
      println(s"  - <emu-table> with header Arguments Type : ${typeTableAlgs.size}")
      println(s"  - <emu-eqn>: ${emuEqns.size}")
    }
    elems
  }
  def getSecId(elem: Element): String =
    toArray(elem.parents).find(_.tagName == "emu-clause").get.id

  ////////////////////////////////////////////////////////////////////////////////
  // grammar
  ////////////////////////////////////////////////////////////////////////////////
  // parse spec.html to Grammar
  def parseGrammar(version: String): (Grammar, Document) = {
    implicit val (lines, document, _) = preprocess(version)
    (parseGrammar, document)
  }
  def parseGrammar(implicit lines: Array[String], document: Document): Grammar =
    GrammarParser(lines, document)

  ////////////////////////////////////////////////////////////////////////////////
  // algorithm
  ////////////////////////////////////////////////////////////////////////////////
  def parseHeads(
    targetSections: Array[String] = Array(),
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    document: Document,
    region: Region
  ): (Map[String, String], List[(Element, List[Head])]) = {
    var res: List[(Element, List[Head])] = List()
    var secIds = (for {
      elem <- getTargetElems(document, false)
      secId = getSecId(elem)
      // TODO handle exception
      heads = HeadParser(elem, detail)
      if !heads.isEmpty
    } yield {
      // if elem is in targets, add it to result
      if (targetSections.contains(secId)) res :+= (elem, heads)
      secId -> heads.head.name
    }).toMap

    (secIds, res)
  }

  // parse spec.html to Algo
  def parseAlgo(
    version: String,
    target: Element,
    detail: Boolean
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    document: Document,
    region: Region
  ): List[Algo] = {
    // get section id of target elements
    val targetSections = getTargetElems(target, detail).map(getSecId(_))

    // parse heads and
    val (secIds, parsedHeads) = parseHeads(targetSections, detail)

    // algorithms
    val (atime, passed) = time(for {
      parsedHead <- parsedHeads
      algos = AlgoParser(version, parsedHead, secIds, detail)
      if !algos.isEmpty
    } yield algos)
    if (detail) println(s"# successful algorithm parsing: ${passed.size} ($atime ms)")
    val algos = passed.toList.flatten
    if (detail) algos.foreach(println _)

    // return algos
    algos
  }

  // parse well-known intrinsic object names
  def parseIntrinsic(implicit document: Document): Set[String] = {
    val table = parseTable("#sec-well-known-intrinsic-objects")
    (for (k <- (1 until table.size)) yield table(k)(0).text.replace("%", "")).toSet
  }

  // parse well-known symbol names
  def parseSymbol(implicit document: Document): Set[String] = {
    val table = parseTable("#sec-well-known-symbols")
    (for (k <- (1 until table.size)) yield table(k)(0).text.replace("@@", "")).toSet
  }

  // get aoids
  def parseAoids(implicit document: Document): Set[String] = {
    toArray(document.select("[aoid]")).map(elem => {
      "[/\\s]".r.replaceAllIn(elem.attr("aoid"), "")
    }).toSet
  }

  // parse section hierarchy
  def parseSection(implicit document: Document): Section = Section(document.body)

  // parse manual algorithms
  def manualAlgos(version: String): Iterable[Algo] = for {
    file <- walkTree(s"$VERSION_DIR/manual-algo") ++ (
      if (BUGFIX) walkTree(s"$VERSION_DIR/bugfix-algo")
      else Nil
    )
    filename = file.toString
    if algoFilter(filename)
  } yield Algo(readFile(file.toString))

  // get symbol names
  private val symbolPattern = "SYMBOL_(.*)".r
  def getSymbols(algos: List[Algo]): Set[String] = {
    var symbols: Set[String] = Set()
    object SymbolExtractor extends UnitWalker {
      override def walk(id: Id) = id.name match {
        case symbolPattern(name) => symbols += name
        case _ =>
      }
    }
    for (algo <- algos) SymbolExtractor.walk(algo.rawBody)
    symbols
  }

  // remove duplicated algorithms
  def removeDup(algos: List[Algo]): List[Algo] = (for {
    algo <- algos
  } yield algo.name -> algo).toMap.values.toList.sortBy(_.name)
}
