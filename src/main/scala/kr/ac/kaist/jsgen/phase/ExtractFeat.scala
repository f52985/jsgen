package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._

// ExtractFeat phase
case object ExtractFeat extends Phase[FeatureVector, ExtractFeatConfig, Feature] {
  val name = "extractFeat"
  val help = "extract feature from AST."

  def apply(
    vec: FeatureVector,
    jsgenConfig: JSGenConfig,
    config: ExtractFeatConfig
  ): Feature = {
    Feature(vec)
  }

  def defaultConfig: ExtractFeatConfig = ExtractFeatConfig()
  val options: List[PhaseOption[ExtractFeatConfig]] = List()
}

// ExtractFeat phase config
case class ExtractFeatConfig() extends Config
