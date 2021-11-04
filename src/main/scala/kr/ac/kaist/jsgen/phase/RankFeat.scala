package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._
import kr.ac.kaist.jsgen.feature.JsonProtocol._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util._

// RankFeat phase
case object RankFeat extends Phase[Unit, RankFeatConfig, Unit] {
  val name = "rank-feat"
  val help = "rank feature for each category."

  implicit val order = Ordering.Double.TotalOrdering
  val JS_PATTERN = "[^/]*\\.js".r

  def parseLine(line: String): Option[(String, (Boolean, Double, Double))] = {
    val tokens = line.split(" ")
    val (file, origResult, compResult, origLen, compLen, origTime, compTime) =
      (tokens(0), tokens(1), tokens(2), tokens(3), tokens(4), tokens(5), tokens(6))

    if (origResult == "FAIL")
      return None

    val sizeRatio = try { compLen.toDouble / origLen.toDouble } catch { case e: Throwable => Double.NaN }
    val timeRatio = try { compTime.toDouble / origTime.toDouble } catch { case e: Throwable => Double.NaN }

    Some(
      (
        JS_PATTERN.findFirstIn(file).get,
        (compResult == "PASS", Math.sqrt(1 / sizeRatio), Math.sqrt(1 / timeRatio))
      )
    )
  }

  def apply(
    unit: Unit,
    jsgenConfig: JSGenConfig,
    config: RankFeatConfig
  ): Unit = {
    val dirname = getFirstFilename(jsgenConfig, "rank feature")

    val vecMap: Map[String, FeatureVector] =
      walkTree(dirname)
        .map(_.toString)
        .filter(extFilter("vec"))
        .map(name => (JS_PATTERN.findFirstIn(name).get, readJson[FeatureVector](name)))
        .toMap

    val features = vecMap.values.foldLeft(Set[Feature]())(_ ++ _.toSet)

    val summary: Map[String, (Boolean, Double, Double)] =
      readFile("lib/desugar/result/summary.txt")
        .split(LINE_SEP)
        .drop(1) //header
        .flatMap(parseLine)
        .toMap

    //def formula(ep: Double, ef: Double, np: Double, nf: Double): Double = ef - ep / (ep + np + 1)
    //def formula(ep: Double, ef: Double, np: Double, nf: Double): Double = ef / (ef + ep + nf)
    def formula(ep: Double, ef: Double, np: Double, nf: Double): Double = ef / (ef + ep) + ef / (ef + nf)

    def semanticScore(feat: Feature): Double = {
      var (ep, ef, np, nf) = (0.0, 0.0, 0.0, 0.0)

      vecMap.foreach(kv => {
        val (file, vec) = kv

        if (summary.contains(file)) {
          if (vec.toSet.contains(feat)) {
            if (summary(file)._1) ep += 1;
            else ef += 1;
          } else {
            if (summary(file)._1) np += 1;
            else nf += 1;
          }
        }
      })

      formula(ep, ef, np, nf)
    }
    def sizeScore(feat: Feature): Double = {
      var (ep, ef, np, nf) = (0.0, 0.0, 0.0, 0.0)

      vecMap.foreach(kv => {
        val (file, vec) = kv

        if (summary.contains(file)) {
          val w = summary(file)._2
          if (!w.isNaN) {
            if (vec.toSet.contains(feat)) {
              ep += w;
              ef += 1 - w;
            } else {
              np += w;
              nf += 1 - w;
            }
          }
        }
      })

      formula(ep, ef, np, nf)
    }
    def timeScore(feat: Feature): Double = {
      var (ep, ef, np, nf) = (0.0, 0.0, 0.0, 0.0)

      vecMap.foreach(kv => {
        val (file, vec) = kv

        if (summary.contains(file)) {
          val w = summary(file)._3
          if (!w.isNaN) {
            if (vec.toSet.contains(feat)) {
              ep += w;
              ef += 1 - w;
            } else {
              np += w;
              nf += 1 - w;
            }
          }
        }
      })

      formula(ep, ef, np, nf)
    }

    val semanticRanking = features.toList.sortBy(semanticScore)
    println("===================")
    println("semantic Ranking")
    println("===================")
    semanticRanking.reverse.take(10).foreach(println)

    val sizeRanking = features.toList.sortBy(sizeScore)
    println("===================")
    println("size Ranking")
    println("===================")
    sizeRanking.reverse.take(10).foreach(println)

    val timeRanking = features.toList.sortBy(timeScore)
    println("===================")
    println("time Ranking")
    println("===================")
    timeRanking.reverse.take(10).foreach(println)
  }

  def defaultConfig: RankFeatConfig = RankFeatConfig()
  val options: List[PhaseOption[RankFeatConfig]] = List()
}

// RankFeat phase config
case class RankFeatConfig() extends Config
