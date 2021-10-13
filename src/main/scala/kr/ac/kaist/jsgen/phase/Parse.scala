package kr.ac.kaist.jsgen.phase

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jsgen.{ BASE_DIR, FEATURE }
import kr.ac.kaist.jsgen.error.NotSupported
import kr.ac.kaist.jsgen.js._
import kr.ac.kaist.jsgen.js.ast.Script
import kr.ac.kaist.jsgen.parser.{ MetaParser, MetaData }
import kr.ac.kaist.jsgen.feature.FeatureVector
import kr.ac.kaist.jsgen.feature.JsonProtocol._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.{ LINE_SEP, JSGenConfig }

// Parse phase
case object Parse extends Phase[Unit, ParseConfig, Script] {
  val name = "parse"
  val help = "parses a JavaScript file using the generated parser."

  def apply(
    unit: Unit,
    jsgenConfig: JSGenConfig,
    config: ParseConfig
  ): Script = {
    val filename = getFirstFilename(jsgenConfig, "parse")
    val ast = parseJS(jsgenConfig.args, config.esparse) match {
      case ast if config.test262 => prependedTest262Harness(filename, ast)
      case ast => ast
    }
    config.jsonFile.foreach(name => {
      val nf = getPrintWriter(name)
      nf.println(ast.toJson.noSpaces)
      nf.close()
    })
    if (config.pprint) println(ast.prettify.noSpaces)
    if (FEATURE) dumpJson(FeatureVector(ast), filename + ".vec")

    ast
  }

  // parse JavaScript files
  def parseJS(list: List[String], esparse: Boolean): Script = list match {
    case List(filename) => parseJS(filename, esparse)
    case _ => mergeStmt(for {
      filename <- list
      script = parseJS(filename, esparse)
      item <- flattenStmt(script)
    } yield item)
  }
  def parseJS(filename: String, esparse: Boolean): Script = {
    if (esparse) {
      val code = parse(executeCmd(s"$BASE_DIR/bin/esparse $filename"))
        .getOrElse(error("invalid AST"))
      Script(code)
    } else {
      Parser.parse(Parser.Script(Nil), fileReader(filename)).get
    }
  }

  // prepend harness.js for Test262
  def prependedTest262Harness(filename: String, script: Script): Script = {
    import Test262._
    val meta = MetaParser(filename)
    val includes = meta.includes
    val includeStmts = includes.foldLeft(basicStmts) {
      case (li, s) => for {
        x <- li
        y <- getInclude(s)
      } yield x ++ y
    } match {
      case Right(l) => l
      case Left(msg) => throw NotSupported(msg)
    }
    val stmts = includeStmts ++ flattenStmt(script)
    mergeStmt(stmts)
  }

  def defaultConfig: ParseConfig = ParseConfig()
  val options: List[PhaseOption[ParseConfig]] = List(
    ("json", StrOption((c, s) => c.jsonFile = Some(s)),
      "dump JSON of AST tree into a file."),
    ("pprint", BoolOption(c => c.pprint = true),
      "pretty print AST tree"),
    ("esparse", BoolOption(c => c.esparse = true),
      "use `esparse` instead of the generated parser."),
    ("test262", BoolOption(c => c.test262 = true),
      "prepend test262 harness files based on metadata."),
    ("feature", BoolOption(c => FEATURE = true),
      "dump feature vector."),
  )
}

// Parse phase config
case class ParseConfig(
  var jsonFile: Option[String] = None,
  var esparse: Boolean = false,
  var test262: Boolean = false,
  var pprint: Boolean = false
) extends Config
