package kr.ac.kaist.jsgen.feature

import kr.ac.kaist.jsgen.js.ASTWalker
import kr.ac.kaist.jsgen.js.ast._

// FeatureVector
case class FeatureVector(var featureVector: List[Feature]) {
  class NonTerminalExtractor extends ASTWalker {
    override def job(ast: AST): Unit = {
      featureVector = Feature(ast.name) :: featureVector
    }

    // Prevent cover-syntaxes from being added to feature
    override def walk(ast: CoverCallExpressionAndAsyncArrowHead): Unit = ()
    override def walk(ast: CoverParenthesizedExpressionAndArrowParameterList): Unit = ()
    override def walk(ast: AssignmentExpression): Unit = ast match {
      case AssignmentExpression4(x0, x2, _, _) if (x0.toString.startsWith("[") || x0.toString.startsWith("{")) =>
        job(ast); walk(x2);
      case _ => super.walk(ast)
    }
  }

  def traverse(ast: AST): Unit = {
    new NonTerminalExtractor().walk(ast)
  }

  def update(visitFunctions: List[String]): Unit = {
    featureVector = visitFunctions.map(Feature) ++ featureVector
  }
}

object FeatureVector {
  def apply(script: Script): FeatureVector = {
    val vec = new FeatureVector(List())
    vec.traverse(script)
    vec
  }
}
