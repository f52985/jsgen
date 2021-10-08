package kr.ac.kaist.jsgen.feature

import kr.ac.kaist.jsgen.js.ASTWalker
import kr.ac.kaist.jsgen.js.ast.{ AST, Script }

// FeatureVector
case class FeatureVector(script: Script) {
  var featureVector: Map[Feature, Boolean] = Map()

  class NonTerminalExtractor extends ASTWalker {
    override def job(ast: AST): Unit = {
      featureVector = featureVector + (Feature(ast.kind) -> true)
    }
  }

  def traverse() = {
    new NonTerminalExtractor().walk(script)
  }
}

object FeatureVector {
  def apply(script: Script): FeatureVector = {
    val vec = new FeatureVector(script)
    vec.traverse()
    vec
  }
}
