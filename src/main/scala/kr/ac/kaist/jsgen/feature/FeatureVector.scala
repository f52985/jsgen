package kr.ac.kaist.jsgen.feature

import kr.ac.kaist.jsgen.js.ast.Script

// FeatureVector
case class FeatureVector(script: Script) {
  var featureVector: Map[Feature, Boolean] = Map()

  def traverse() = {
    featureVector = Map(Feature("Script") -> true, Feature("VarDecl") -> false)
  }
}

object FeatureVector {
  def apply(script: Script): FeatureVector = {
    val vec = new FeatureVector(script)
    vec.traverse()
    vec
  }
}
