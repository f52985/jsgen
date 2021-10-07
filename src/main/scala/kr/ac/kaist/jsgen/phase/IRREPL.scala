package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.{ LINE_SEP, JSGenConfig }

// IRREPL phase
case object IRREPL extends Phase[State, IRREPLConfig, Unit] {
  val name = "repl-ir"
  val help = "performs REPL for IR instructions."

  def apply(
    st: State,
    jsgenConfig: JSGenConfig,
    config: IRREPLConfig
  ): Unit = REPL(st)

  def defaultConfig: IRREPLConfig = IRREPLConfig()
  val options: List[PhaseOption[IRREPLConfig]] = List()
}

// IRREPL phase config
case class IRREPLConfig() extends Config
