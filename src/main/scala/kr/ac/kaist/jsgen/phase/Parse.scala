package kr.ac.kaist.jsgen.phase

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jsgen.{ BASE_DIR, FEATURE }
import kr.ac.kaist.jsgen.error.NotSupported
import kr.ac.kaist.jsgen.js._
import kr.ac.kaist.jsgen.js.ast._
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
    var ast = parseJS(jsgenConfig.args, config.esparse)

    if (config.test262)
      ast = prependedTest262Harness(filename, ast, config.removeAssert)

    if (config.removeAssert)
      ast = removeAssert(ast)

    config.jsonFile.foreach(name =>
      dumpFile(ast.toJson.noSpaces, name))

    if (config.pprint)
      println(ast.prettify.noSpaces)

    if (FEATURE)
      dumpJson(FeatureVector(ast), filename + ".vec")

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
  def prependedTest262Harness(filename: String, script: Script, removeAssert: Boolean): Script = {
    import Test262._
    val meta = MetaParser(filename)
    var includes = meta.includes
    if (removeAssert) includes = includes.filterNot(inc =>
      List(
        "deepEqual.js",
        "compareArray.js",
        "compareIterator.js",
        "assertRelativeDateMs.js",
        "propertyHelper.js",
        "promiseHelper.js"
      ).contains(inc))
    val baseStmts = if (removeAssert) Right(List()) else basicStmts
    val includeStmts = includes.foldLeft(baseStmts) {
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

  // remove asserts for Test262
  // TODO: span?
  def removeAssert(script: Script): Script = {
    implicit def downcast[T <: AST](ast: AST): T = ast.asInstanceOf[T]
    implicit def downcastOption[T <: AST](ast: Option[AST]): Option[T] = ast.map(downcast[T])

    def getCall(ast: AST): Option[CoverCallExpressionAndAsyncArrowHead] = {
      val ty = "CoverCallExpressionAndAsyncArrowHead"
      if (ast.getKinds.contains(ty))
        ast.getElems(ty).lift(0)
      else
        None
    }

    def getArgument(ast: Arguments, n: Int): Option[AssignmentExpression] =
      ast.getElems("ArgumentList")(0).getElems("AssignmentExpression").lift(n)

    val asserts = Map(
      //assert.js
      "assert" -> 1,
      "assert . sameValue" -> 1,
      "assert . notSameValue" -> 1,

      //deepEqual.js
      "assert . deepEqual" -> 1,

      //compareArray.js
      "assert . compareArray" -> 1,

      //compareIterator.js
      "assert . compareIterator" -> 1,

      //assertRelativeDateMs.js
      "assertRelativeDateMs" -> 1,

      //sta.js
      "$ERROR" -> 0,
      "$DONOTEVALUATE" -> 0,

      //propertyHelper.js
      "verifyProperty" -> 3,
      "verifyEqualTo" -> 2,
      "verifyWritable" -> 3,
      "verifyNotWritable" -> 3,
      "verifyEnumerable" -> 2,
      "verifyNotEnumerable" -> 2,
      "verifyConfigurable" -> 2,
      "verifyNotConfigurable" -> 2,

      //promiseHelper.js
      "checkSequence" -> 1,
      "checkSettledPromises" -> 1,
    )

    def transformStmt(ast: StatementListItem): List[StatementListItem] = {
      getCall(ast).flatMap(call => call match {
        case CoverCallExpressionAndAsyncArrowHead0(x0, x1, _, _) =>
          val func = x0.toString
          if (asserts.contains(func)) {
            val argNum = asserts(func)
            val args = List.from(0 until argNum).flatMap(idx => getArgument(x1, idx).map(_.toString))
            Some(args)
          } else if (x0.toString == "assert . throws")
            Some(getArgument(x1, 1).toList.map(callback => s"try { ($callback)(); } catch {}"))
          else
            None
        case _ =>
          None
      })
        .map(stmts => stmts.map(stmt => Parser.parse(Parser.StatementListItem(ast.parserParams), stmt).get))
        .getOrElse(List(ast))
    }

    class AssertRemover extends ASTTransformer {
      def handleAllStmt(l: StatementList): Option[StatementList] =
        mergeStmtList(flattenStmtList(l).flatMap(transformStmt))

      override def transform(ast: Block): Block = ast match {
        case Block0(l, p, s) => super.transform(Block0(l.flatMap(handleAllStmt), p, s))
      }
      override def transform(ast: CaseClause): CaseClause = ast match {
        case CaseClause0(e, l, p, s) => super.transform(CaseClause0(e, l.flatMap(handleAllStmt), p, s))
      }
      override def transform(ast: DefaultClause): DefaultClause = ast match {
        case DefaultClause0(l, p, s) => super.transform(DefaultClause0(l.flatMap(handleAllStmt), p, s))
      }
      override def transform(ast: FunctionStatementList): FunctionStatementList = ast match {
        case FunctionStatementList0(l, p, s) => super.transform(FunctionStatementList0(l.flatMap(handleAllStmt), p, s))
      }

      override def transform(ast: Script): Script =
        super.transform(mergeStmt(flattenStmt(script).flatMap(transformStmt)))
    }

    new AssertRemover().transform(script)
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
    ("remove-assert", BoolOption(c => c.removeAssert = true),
      "remove test262 asserts."),
  )
}

// Parse phase config
case class ParseConfig(
  var jsonFile: Option[String] = None,
  var esparse: Boolean = false,
  var test262: Boolean = false,
  var pprint: Boolean = false,
  var removeAssert: Boolean = false
) extends Config
