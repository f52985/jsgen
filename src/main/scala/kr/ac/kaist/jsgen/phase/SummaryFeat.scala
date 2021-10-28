package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.feature._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util._

// SummaryFeat phase
case object SummaryFeat extends Phase[Map[String, Feature], SummaryFeatConfig, Unit] {
  val name = "sumamry-feat"
  val help = "summarize information per feature."

  private def toCntMap(elems: Iterable[String]): Map[String, Int] = {
    val mtMap = Map[String, Int]().withDefaultValue(0)
    elems.foldLeft(mtMap)({
      case (cur, elem) => cur + (elem -> (cur(elem) + 1))
    })
  }

  private def count(cnt: Map[String, Int], files: Iterable[String]): Int = files.foldLeft(0)(_ + cnt(_))

  private def getAvgLen(files: Iterable[String], prefix: String): Double = {
    val existingFiles =
      files
        .map(prefix + _)
        .filter(exists)

    val sum: Double = existingFiles.foldLeft(0)(_ + readFile(_).length)
    val cnt: Double = existingFiles.toList.length
    sum / cnt
  }

  private def getAvgTime(timeMap: Map[String, Double], files: Iterable[String], prefix: String): Double = {
    val (sum, cnt) = files.map(prefix + _).foldLeft((0.0, 0))({
      case ((sum: Double, cnt: Int), f: String) => if (timeMap contains f) (sum + timeMap(f), cnt + 1) else (sum, cnt)
    })

    if (cnt > 0) sum / cnt else 0
  }

  def apply(
    featMap: Map[String, Feature],
    jsgenConfig: JSGenConfig,
    config: SummaryFeatConfig
  ): Unit = {
    val features = featMap.foldLeft(Set[Feature]())({ case (features, (_, feat)) => features + feat })

    val origResultFile = if (jsgenConfig.noHarness) "no-harness.txt" else "test262.txt"
    val compResultFile = if (jsgenConfig.noHarness) "no-harness-compiled.txt" else "compiled-test262.txt"

    val origResult = readFile(s"$DESUGAR_RESULT_DIR/$origResultFile").split(LINE_SEP)
    val compResult = readFile(s"$DESUGAR_RESULT_DIR/$compResultFile").split(LINE_SEP)

    val speedResult = readFile(s"$DESUGAR_RESULT_DIR/speed.txt").split(LINE_SEP)

    val PREFIX_LEN = 12 // lib/desugar/
    val jsPattern = """script/.*\.js""".r

    val totalCnt = toCntMap(compResult.flatMap(jsPattern.findFirstIn))
    val origFailCnt = toCntMap(origResult.filter(_ contains "FAIL").flatMap(jsPattern.findFirstIn))
    val compFailCnt = toCntMap(compResult.filter(_ contains "FAIL").flatMap(jsPattern.findFirstIn))

    val timeMap = speedResult.filter(_ contains "script").map(line => {
      val f = line.split(" ")(0)
      val time = line.split(" ")(1).toDouble
      (f, time)
    }).toMap

    println("feat total origFail compFail diff ratio origLen commpLen origTime compTime")

    features.foreach(feat => {
      val files = featMap.filter(_._2 == feat).keys.map(_.drop(PREFIX_LEN))
      val total = count(totalCnt, files)
      val origFail = count(origFailCnt, files)
      val compFail = count(compFailCnt, files)
      val diff = compFail - origFail
      val ratio = diff * 100.0 / total
      val NH_PREFIX = if (jsgenConfig.noHarness) "no-harness-" else ""
      val origLen = getAvgLen(files, s"$DESUGAR_DIR/${NH_PREFIX}min-")
      val compLen = getAvgLen(files, s"$DESUGAR_DIR/${NH_PREFIX}min-compiled-")
      val origTime = getAvgTime(timeMap, files, NH_PREFIX)
      val compTime = getAvgTime(timeMap, files, NH_PREFIX + "compiled-")

      println(s"$feat $total $origFail $compFail $diff $ratio% $origLen $compLen $origTime $compTime")
    })
  }

  def defaultConfig: SummaryFeatConfig = SummaryFeatConfig()
  val options: List[PhaseOption[SummaryFeatConfig]] = List()
}

// SummaryFeat phase config
case class SummaryFeatConfig() extends Config
