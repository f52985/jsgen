package kr.ac.kaist.jsgen.phase
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.{ LINE_SEP, JSGenConfig }
import kr.ac.kaist.jsgen.util.JvmUseful._

// IRParse phase
case object IRParse extends Phase[Unit, IRParseConfig, Program] {
  val name = "parse-ir"
  val help = "parses an IR file."

  def apply(
    unit: Unit,
    jsgenConfig: JSGenConfig,
    config: IRParseConfig
  ): Program = {
    val filename = getFirstFilename(jsgenConfig, "parse-ir")
    Program.fromFile(filename)
  }

  def defaultConfig: IRParseConfig = IRParseConfig()
  val options: List[PhaseOption[IRParseConfig]] = List()
}

// IRParse phase config
case class IRParseConfig() extends Config
