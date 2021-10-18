package kr.ac.kaist.jsgen.feature

import kr.ac.kaist.jsgen.js.ASTWalker
import kr.ac.kaist.jsgen.js.ast._

// FeatureVector
case class FeatureVector(var featureVector: List[Feature]) {
  class NonTerminalExtractor extends ASTWalker {
    override def job(ast: AST): Unit = {
      featureVector = Feature(ast.name) :: featureVector
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
