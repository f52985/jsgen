package kr.ac.kaist.jsgen.analyzer.command

import kr.ac.kaist.jsgen.analyzer._
import kr.ac.kaist.jsgen.cfg._
import kr.ac.kaist.jsgen.util.Useful._

// find-merged command
case object CmdFindMerged extends Command(
  "find-merged", "Find merged analysis results."
) {
  // options
  val options = Nil

  // run command
  def apply(
    repl: REPL,
    cpOpt: Option[ControlPoint],
    args: List[String]
  ): Unit = cpOpt.map(cp => {
    val st = cp match {
      case np: NodePoint[Node] => repl.sem(np)
      case rp: ReturnPoint => repl.sem(rp).state
    }
    st.findMerged
  })
}
