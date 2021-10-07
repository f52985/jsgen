package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen.{ JSGen, JSGenConfig }

// Help phase
case object Help extends Phase[Unit, HelpConfig, Unit] {
  val name = "help"
  val help = "shows help messages."

  def apply(
    unit: Unit,
    jsgenConfig: JSGenConfig,
    config: HelpConfig
  ): Unit = println(JSGen.help)
  def defaultConfig: HelpConfig = HelpConfig()
  val options: List[PhaseOption[HelpConfig]] = Nil
}

case class HelpConfig() extends Config
