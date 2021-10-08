package kr.ac.kaist.jsgen.feature

case class Feature(feat: String)

object Feature {
  def apply(vec: FeatureVector): Feature = {
    Feature("Script")
  }
}
