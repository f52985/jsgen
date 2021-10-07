package kr.ac.kaist.jsgen.analyzer.command

import kr.ac.kaist.jsgen.analyzer._

// continue command
case object CmdContinue extends Command(
  "continue", "Continue static analysis."
) {
  // options
  val options: List[String] = Nil

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = repl.continue = true
}
