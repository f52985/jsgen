package kr.ac.kaist.jsgen.phase

import java.io.File
import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._
import kr.ac.kaist.jsgen.feature.JsonProtocol._
import kr.ac.kaist.jsgen.js.ast._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.JvmUseful._

// ExtractVec phase
case object ExtractVec extends Phase[Map[File, Script], ExtractVecConfig, Map[File, FeatureVector]] {
  val name = "extract-vec"
  val help = "extract feature vector from AST."

  def apply(
    scripts: Map[File, Script],
    jsgenConfig: JSGenConfig,
    config: ExtractVecConfig
  ): Map[File, FeatureVector] = {
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
