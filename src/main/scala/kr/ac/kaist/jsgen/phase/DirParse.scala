package kr.ac.kaist.jsgen.phase

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jsgen.js._
import kr.ac.kaist.jsgen.js.ast.Script
import kr.ac.kaist.jsgen.feature._
import kr.ac.kaist.jsgen.feature.JsonProtocol._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.JSGenConfig

// DirParse phase
case object DirParse extends Phase[Unit, DirParseConfig, Unit] {
  val name = "parse-dir"
  val help = "parses all JavaScript files in the given directory"

  def apply(
    unit: Unit,
    jsgenConfig: JSGenConfig,
    config: DirParseConfig
  ): Unit = {
    val dirname = getFirstFilename(jsgenConfig, "parse directory")

    walkTree(dirname)
      .map(_.toString)
      .filter(jsFilter)
      .foreach(name => {
        parseJS(name).foreach(ast => dumpJson(FeatureVector(ast), name + ".vec"))
      })
  }

  private var cnt = 0
  private lazy val st = System.currentTimeMillis

  // parse a JavaScript file
  def parseJS(filename: String): Option[Script] = {
    if (cnt % 100 == 0) {
      val cur = System.currentTimeMillis
      //println(s"Parsed $cnt files in ${(cur - st) / 1000} sec.")
    }
    cnt += 1

    val result = Parser.parse(Parser.Script(Nil), fileReader(filename))
    if (result.successful)
      Some(result.get)
    else {
      println("Parse fail: " + filename)
      None
    }
  }

  def defaultConfig: DirParseConfig = DirParseConfig()
  val options: List[PhaseOption[DirParseConfig]] = List()
}

// DirParse phase config
case class DirParseConfig() extends Config
