package kr.ac.kaist.jsgen.phase

import java.io.File
import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._

// ExtractFeat phase
case object ExtractFeat extends Phase[Map[File, FeatureVector], ExtractFeatConfig, Map[File, Feature]] {
  val name = "extract-feat"
  val help = "extract feature from AST."

  private type FeatCnt = Map[Feature, Int]
  implicit val order = Ordering.Float.TotalOrdering

  def apply(
    vecs: Map[File, FeatureVector],
    jsgenConfig: JSGenConfig,
    config: ExtractFeatConfig
  ): Map[File, Feature] = {
    // Count number of occurence of each feature
    var totalFeatCnt: FeatCnt = Map()
    val fileFeatCnt: Map[File, FeatCnt] = vecs.map({
      case (file, vec) => {
        var featCnt: FeatCnt = Map()
        vec.featureVector.foreach(feat => {
          featCnt = featCnt + (feat -> (featCnt.getOrElse(feat, 0) + 1))
          totalFeatCnt = totalFeatCnt + (feat -> (totalFeatCnt.getOrElse(feat, 0) + 1))
        })
        (file, featCnt)
      }
    })

    println(totalFeatCnt)

    val ret = vecs.map({
      case (file, vec) => (file, vec.featureVector.maxBy(feat => {
        fileFeatCnt(file)(feat).toFloat / totalFeatCnt(feat)
      }))
    })
    println(ret)
    ret
  }

  def defaultConfig: ExtractFeatConfig = ExtractFeatConfig()
  val options: List[PhaseOption[ExtractFeatConfig]] = List()
}

// ExtractFeat phase config
case class ExtractFeatConfig() extends Config
