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
    val jsname = st.fnameOpt.get
    val fname = if (jsgenConfig.noHarness) jsname + ".no-harness" else jsname

    val injected: String = Injector(fname, st).result

    if (config.dump)
      dumpFile(injected, jsname + ".injected")

    injected
  }

  def defaultConfig: InjectConfig = InjectConfig()
  val options: List[PhaseOption[InjectConfig]] = List(
    ("dump", BoolOption(c => c.dump = true),
      "dump injected script"),
  )
}

// Inject phase config
case class InjectConfig(
  var dump: Boolean = false
) extends Config
