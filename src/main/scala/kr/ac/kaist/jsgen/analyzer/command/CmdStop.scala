package kr.ac.kaist.jsgen.analyzer.command

import kr.ac.kaist.jsgen.analyzer._
import kr.ac.kaist.jsgen.util.Useful._

// stop command
case object CmdStop extends Command(
  "stop", "Stop the repl."
) {
  // options
  val options = Nil

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = repl.stop
}
