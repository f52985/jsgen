package kr.ac.kaist.jsgen.js

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.error.NotSupported
import kr.ac.kaist.jsgen.js.ast._
import kr.ac.kaist.jsgen.phase.FilterMeta
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util.JvmUseful._

object Test262 {
  // parsing result
  type ParseResult = Either[String, List[StatementListItem]]

  // cache for parsing results for necessary harness files
  val getInclude = cached[String, ParseResult](name => try {
    val filename = s"$TEST262_DIR/harness/$name"
    val script = parseFile(filename)
    Right(flattenStmt(script))
  } catch {
    case NotSupported(msg) => Left(msg)
  })

  // parse JavaScript file
  def parseFile(filename: String): Script =
    Parser.parse(Parser.Script(Nil), fileReader(filename)).get

  // test262 test configuration
  lazy val config = FilterMeta.test262configSummary

  // basic statements
  lazy val basicStmts = for {
    x <- getInclude("assert.js")
    y <- getInclude("sta.js")
  } yield x ++ y

  class AssertRemover extends ASTTransformer {
    import AssertRemover._

    def transformStmt(ast: StatementListItem): List[StatementListItem] = {
      asCall(ast).flatMap(call => call match {
        case CoverCallExpressionAndAsyncArrowHead0(x0, x1, _, _) =>
          val func = x0.toString
          if (asserts.contains(func)) {
            val argNum = asserts(func)
            val args = (0 until argNum).toList.flatMap(idx => getArgument(x1, idx)).map(_.toString)
            Some(args)
          } else if (func == ASSERT_THROWS)
            Some(getArgument(x1, 1).toList.map(callback => s"try { ($callback)(); } catch {}"))
          else
            None
        case _ =>
          None
      })
        .map(stmts => stmts.map(stmt => {
          val parser = Parser.StatementListItem(ast.parserParams)
          val illegal = List("{", "function", "async function", "class", "let [")
          val str = if (illegal.exists(stmt.startsWith(_))) s"($stmt)" else stmt
          Parser.parse(parser, str).get
        }))
        .getOrElse(List(ast))
    }

    def asCall(ast: AST): Option[CoverCallExpressionAndAsyncArrowHead] = {
      val ty = "CoverCallExpressionAndAsyncArrowHead"
      if (ast.getKinds.contains(ty))
        ast.getElems(ty).lift(0)
      else
        None
    }

    def getArgument(ast: Arguments, n: Int): Option[AssignmentExpression] =
      ast.getElems("ArgumentList")(0).getElems("AssignmentExpression").lift(n)

    // handle StatementList
    def handleAllStmt(l: StatementList, p: List[Boolean]): Option[StatementList] =
      mergeStmtList(flattenStmtList(l).flatMap(transformStmt), p)

    override def transform(ast: Block): Block = ast match {
      case Block0(l, p, s) => super.transform(Block0(l.flatMap(handleAllStmt(_, p)), p, s))
    }
    override def transform(ast: CaseClause): CaseClause = ast match {
      case CaseClause0(e, l, p, s) => super.transform(CaseClause0(e, l.flatMap(handleAllStmt(_, p)), p, s))
    }
    override def transform(ast: DefaultClause): DefaultClause = ast match {
      case DefaultClause0(l, p, s) => super.transform(DefaultClause0(l.flatMap(handleAllStmt(_, p)), p, s))
    }
    override def transform(ast: FunctionStatementList): FunctionStatementList = ast match {
      case FunctionStatementList0(l, p, s) => super.transform(FunctionStatementList0(l.flatMap(handleAllStmt(_, p)), p, s))
    }

    override def transform(ast: Script): Script =
      super.transform(mergeStmt(flattenStmt(ast).flatMap(transformStmt)))

    // handle Statement not handled by StatementList
    override def transform(ast: Statement): Statement = {
      asCall(ast).flatMap(call => call match {
        case CoverCallExpressionAndAsyncArrowHead0(x0, x1, _, _) =>
          if (predicates.exists(_ == x0.toString))
            getArgument(x1, 0)
          else
            None
      })
        .map(arg => Parser.parse(Parser.Statement(ast.parserParams), arg.toString).get)
        .getOrElse(super.transform(ast))
    }
  }
  object AssertRemover {
    def apply(script: Script): Script = ???

    implicit def downcast[T <: AST](ast: AST): T = ast.asInstanceOf[T]
    implicit def downcastOption[T <: AST](ast: Option[AST]): Option[T] = ast.map(downcast[T])

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
    val ASSERT_THROWS = "assert . throws"
    val predicates = Set("compareArray", "arrayContains")
  }

  // harness remover
  class HarnessRemover extends AssertRemover {
    import HarnessRemover._

    // $DONE => function () {}, Test262Error => Error
    override def transform(ast: PrimaryExpression): PrimaryExpression = ast match {
      case PrimaryExpression1(x0, ps, _) if x0.toString == $DONE =>
        Parser.parse(Parser.PrimaryExpression(ps), "(function(){})").get
      case PrimaryExpression1(x0, ps, _) if x0.toString == TEST262_ERROR =>
        Parser.parse(Parser.PrimaryExpression(ps), "URIError").get
      case _ => super.transform(ast)
    }
  }
  object HarnessRemover {
    // remove harness
    def apply(script: Script): Script = {
      (new HarnessRemover).transform(script)
    }

    // constants
    val TEST262_ERROR = "Test262Error"
    val $DONE = "$DONE"
  }
}
