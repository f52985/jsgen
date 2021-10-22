package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util._

// SummaryFeat phase
case object SummaryFeat extends Phase[Map[String, Feature], SummaryFeatConfig, Unit] {
  val name = "sumamry-feat"
  val help = "summarize information per feature."

  //TODO: faster
  private def count(msgs: Iterable[String], files: Iterable[String]) = msgs.count(line =>
    files.exists(file => line contains file))

  def apply(
    featMap: Map[String, Feature],
    jsgenConfig: JSGenConfig,
    config: SummaryFeatConfig
  ): Unit = {
    val features = featMap.foldLeft(Set[Feature]())({ case (features, (_, feat)) => features + feat })

    val origResult = readFile(s"$DESUGAR_RESULT_DIR/test262.txt").split(LINE_SEP)
    val compResult = readFile(s"$DESUGAR_RESULT_DIR/compiled-test262.txt").split(LINE_SEP)
    val PREFIX_LEN = 12 // lib/desugar/

    println("feat total origFail compFail diff ratio")

    features.foreach(feat => {
      val files = featMap.filter(_._2 == feat).keys.map(_.drop(PREFIX_LEN))
      val total = count(origResult, files)
      val origFail = count(origResult.filter(_ contains "FAIL"), files)
      val compFail = count(compResult.filter(_ contains "FAIL"), files)
      val diff = compFail - origFail
      val ratio = diff * 100.0 / total

      println(s"$feat $total $origFail $compFail $diff $ratio%")
    })
  }

  def defaultConfig: SummaryFeatConfig = SummaryFeatConfig()
  val options: List[PhaseOption[SummaryFeatConfig]] = List()
}

// SummaryFeat phase config
case class SummaryFeatConfig() extends Config
