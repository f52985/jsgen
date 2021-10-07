package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.JvmUseful._

// IREval phase
case object IREval extends Phase[State, IREvalConfig, State] {
  val name: String = "eval-ir"
  val help: String = "evaluates a given IR state."

  def apply(
    st: State,
    jsgenConfig: JSGenConfig,
    config: IREvalConfig
  ): State = Interp(st, config.timeout)

  def defaultConfig: IREvalConfig = IREvalConfig()
  val options: List[PhaseOption[IREvalConfig]] = List(
    ("timeout", NumOption((c, i) => c.timeout = if (i == 0) None else Some(i)),
      "set timeout of interpreter(second), 0 for unlimited.")
  )
}

// IREval phase config
case class IREvalConfig(
  var timeout: Option[Long] = Some(TIMEOUT)
) extends Config
