package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._
import kr.ac.kaist.jsgen.feature.JsonProtocol._
import kr.ac.kaist.jsgen.js.ast._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.JvmUseful._

// ExtractVec phase
case object ExtractVec extends Phase[Map[String, Script], ExtractVecConfig, Map[String, FeatureVector]] {
  val name = "extract-vec"
  val help = "extract feature vector from AST."

  def apply(
    scripts: Map[String, Script],
    jsgenConfig: JSGenConfig,
    config: ExtractVecConfig
  ): Map[String, FeatureVector] = {
    val vectors = scripts.map({ case (k, v) => (k, FeatureVector(v)) }).toMap
    if (config.dump) {
      vectors.foreach({
        case (file, vec) =>
          dumpJson(vec, file.toString + ".vec")
      })
    }
    vectors
  }

  def defaultConfig: ExtractVecConfig = ExtractVecConfig()
  val options: List[PhaseOption[ExtractVecConfig]] = List(
    ("dump", BoolOption(c => c.dump = true),
      "dump feature vector into file.")
  )
}

// ExtractVec phase config
case class ExtractVecConfig(
  var dump: Boolean = false
) extends Config
