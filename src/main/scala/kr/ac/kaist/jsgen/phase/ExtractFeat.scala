package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._
import kr.ac.kaist.jsgen.feature.JsonProtocol._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util._
import scala.collection.mutable.{ Map => MMap }

// ExtractFeat phase
case object ExtractFeat extends Phase[Unit, ExtractFeatConfig, Unit] {
  val name = "extract-feat"
  val help = "extract feature from AST."

  private type FeatCnt = MMap[Feature, Int]
  implicit val order = Ordering.Double.TotalOrdering

  def apply(
    unit: Unit,
    jsgenConfig: JSGenConfig,
    config: ExtractFeatConfig
  ): Unit = {
    val dirname = getFirstFilename(jsgenConfig, "extract feature")
    val vecs = walkTree(dirname)
      .map(_.toString)
      .filter(extFilter("vec"))
      .map(name => (name, readJson[FeatureVector](name)))
      .toMap

    // Count number of occurence of each feature
    var totalFeatCnt: FeatCnt = MMap().withDefaultValue(0)
    var df: FeatCnt = MMap().withDefaultValue(0)
    val fileFeatCnt: Map[String, FeatCnt] = vecs.map({
      case (filename, vec) => {
        var featCnt: FeatCnt = MMap().withDefaultValue(0)
        vec.featureVector.foreach(feat => {
          featCnt(feat) += 1
          totalFeatCnt(feat) += 1
        })
        featCnt.foreach(p => df(p._1) += 1)
        (filename, featCnt)
      }
    })

    if (!jsgenConfig.silent)
      println(totalFeatCnt)

    val ret = vecs.map({
      case (filename, vec) => (filename, vec.featureVector.maxBy(feat => {
        //fileFeatCnt(filename)(feat).toDouble / totalFeatCnt(feat)
        fileFeatCnt(filename)(feat) * math.log(df.size / df(feat))
      }))
    })

    if (!jsgenConfig.silent)
      println(ret)

    if (config.dump) {
      ret.foreach({
        case (vecname, feat) => {
          val featname = vecname.dropRight(4) + ".feat"
          dumpFile(feat.toString + LINE_SEP, featname)
        }
      })
    }
  }

  def defaultConfig: ExtractFeatConfig = ExtractFeatConfig()
  val options: List[PhaseOption[ExtractFeatConfig]] = List(
    ("dump", BoolOption(c => c.dump = true),
      "dump feature to file."),
  )
}

// ExtractFeat phase config
case class ExtractFeatConfig(
  var dump: Boolean = false
) extends Config
