package kr.ac.kaist.jsgen.feature

case class Feature(feat: String) {
  override def toString() = feat
}

object Feature {
  def apply(vec: FeatureVector): Feature = {
    Feature("Script")
  }
}
