package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.injector.Injector
import kr.ac.kaist.jsgen.util._
//import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.ir.State

// Inject phase
case object Inject extends Phase[State, InjectConfig, String] {
  val name = "inject"
  val help = "constructs tests by injecting semantics assertions to given JavaScript program."

  def apply(
    st: State,
    jsgenConfig: JSGenConfig,
    config: InjectConfig
  ): String = {
    val injected: String = Injector(st).result
    config.dump.foreach(dumpFile(injected, _))
    injected
  }

  def defaultConfig: InjectConfig = InjectConfig()
  val options: List[PhaseOption[InjectConfig]] = List(
    ("dump", StrOption((c, s) => c.dump = Some(s)),
      "dump injected script into the given file."),
  )
}

// Inject phase config
case class InjectConfig(
  var dump: Option[String] = None
) extends Config
