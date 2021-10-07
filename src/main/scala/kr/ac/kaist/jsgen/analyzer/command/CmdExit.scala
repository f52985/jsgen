package kr.ac.kaist.jsgen.analyzer.command

import kr.ac.kaist.jsgen.analyzer._
import kr.ac.kaist.jsgen.util.Useful._

// exit command
case object CmdExit extends Command(
  "exit",
  "Exit the type checking."
) {
  // options
  val options = Nil

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = error("stop for debugging")
}
