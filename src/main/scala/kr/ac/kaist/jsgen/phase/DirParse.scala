package kr.ac.kaist.jsgen.phase

import java.io.File
import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jsgen.BASE_DIR
import kr.ac.kaist.jsgen.error.NotSupported
import kr.ac.kaist.jsgen.js._
import kr.ac.kaist.jsgen.js.ast.Script
import kr.ac.kaist.jsgen.parser.{ MetaParser, MetaData }
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.{ LINE_SEP, JSGenConfig }

// DirParse phase
case object DirParse extends Phase[Unit, DirParseConfig, Map[File, Script]] {
  val name = "parse-dir"
  val help = "parses all JavaScript files in the given directory"

  def apply(
    unit: Unit,
    jsgenConfig: JSGenConfig,
    config: DirParseConfig
  ): Map[File, Script] = {
    val dirname = getFirstFilename(jsgenConfig, "parse directory")

    walkTree(dirname)
      .filter(f => jsFilter(f.toString))
      .map(f => (f, parseJS(f.toString)))
      .toMap
  }

  private val EMPTY_SCRIPT = Parser.parse(Parser.Script(Nil), "").get
  private var cnt = 0
  private lazy val st = System.currentTimeMillis

  // parse JavaScript file
  def parseJS(filename: String): Script = {
    if (cnt % 100 == 0) {
      val cur = System.currentTimeMillis
      println(s"Parsed $cnt files in ${(cur - st) / 1000} sec.")
    }
    cnt += 1

    Parser.parse(Parser.Script(Nil), fileReader(filename)).getOrElse({
      println("Parse fail: " + filename)
      EMPTY_SCRIPT
    })
  }

  def defaultConfig: DirParseConfig = DirParseConfig()
  val options: List[PhaseOption[DirParseConfig]] = List()
}

// DirParse phase config
case class DirParseConfig() extends Config
