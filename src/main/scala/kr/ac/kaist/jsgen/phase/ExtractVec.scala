package kr.ac.kaist.jsgen.phase

import java.io.File
import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._
import kr.ac.kaist.jsgen.js.ast._

// ExtractVec phase
case object ExtractVec extends Phase[Map[File, Script], ExtractVecConfig, FeatureVector] {
  val name = "extractVec"
  val help = "extract feature vector from AST."

  def apply(
    scripts: Map[File, Script],
    jsgenConfig: JSGenConfig,
    config: ExtractVecConfig
  ): FeatureVector = {
    FeatureVector(scripts.values.head)
  }

  def defaultConfig: ExtractVecConfig = ExtractVecConfig()
  val options: List[PhaseOption[ExtractVecConfig]] = List()
}

// ExtractVec phase config
case class ExtractVecConfig() extends Config
