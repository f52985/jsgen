package kr.ac.kaist.jsgen.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.jsgen.JSGenConfig
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util._

// IRLoad phase
case object IRLoad extends Phase[Program, IRLoadConfig, State] {
  val name: String = "load-ir"
  val help: String = "loads an IR AST to the initial IR states."

  def apply(
    program: Program,
    jsgenConfig: JSGenConfig,
    config: IRLoadConfig
  ): State = State(InstCursor).moveTo(program)

  def defaultConfig: IRLoadConfig = IRLoadConfig()
  val options: List[PhaseOption[IRLoadConfig]] = List()
}

// IRLoad phase config
case class IRLoadConfig() extends Config
