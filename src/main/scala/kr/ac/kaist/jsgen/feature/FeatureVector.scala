package kr.ac.kaist.jsgen.feature

import kr.ac.kaist.jsgen.js.ASTWalker
import kr.ac.kaist.jsgen.js.ast._

// FeatureVector
case class FeatureVector(var featureVector: List[Feature]) {
  class NonTerminalExtractor extends ASTWalker {
    override def job(ast: AST): Unit = {
      featureVector = Feature(ast.name) :: featureVector
    }

    override def walk(ast: CoverCallExpressionAndAsyncArrowHead): Unit = ast match {
      case CoverCallExpressionAndAsyncArrowHead0(x0, x1, _, _) =>
        val asserts = List("assert", "assert . sameValue", "assert . notSamevalue")
        if (asserts.contains(x0.toString)) {
          popAssert()
          super.walk(getArgument(x1, 0))
        } else if (x0.toString == "assert . throws") {
          popAssert()
          super.walk(getArgument(x1, 1))
        } else super.walk(ast)
      case _ =>
        super.walk(ast)
    }

    def getArgument(ast: Arguments, n: Int): AssignmentExpression = ast match {
      case Arguments0(_, _) =>
        throw new Exception("wrong use of test262 assert")
      case Arguments1(x1, _, _) =>
        x1.reverse(n)
      case Arguments2(x1, _, _) =>
        x1.reverse(n)
    }

    implicit def arglist2list(arglist: ArgumentList): List[AssignmentExpression] = arglist match {
      case ArgumentList0(x0, _, _) => List(x0)
      case ArgumentList2(x0, x2, _, _) => x2 :: x0
      case _ => throw new Exception("wrong use of test262 assert")
    }

    def popAssert(): Unit = {
      while (featureVector.head.feat != "AssignmentExpression0")
        featureVector = featureVector.tail
      featureVector = featureVector.tail
    }
  }

  def traverse(script: Script) = {
    new NonTerminalExtractor().walk(script)
  }
}

object FeatureVector {
  def apply(script: Script): FeatureVector = {
    val vec = new FeatureVector(List())
    vec.traverse(script)
    vec
  }
}
