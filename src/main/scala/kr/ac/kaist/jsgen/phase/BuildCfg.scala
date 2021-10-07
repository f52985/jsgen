package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.cfg._
import kr.ac.kaist.jsgen.JSGenConfig
import kr.ac.kaist.jsgen.spec.ECMAScript
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util.Useful._

// BuildCFG phase
case object BuildCFG extends Phase[ECMAScript, BuildCFGConfig, CFG] {
  val name = "build-cfg"
  val help = "builds control flow graph (CFG)."

  def apply(
    spec: ECMAScript,
    jsgenConfig: JSGenConfig,
    config: BuildCFGConfig
  ): CFG = {
    val (cfgTime, cfg) = time("build CFG", new CFG(spec))

    if (config.dot) {
      mkdir(CFG_LOG_DIR)
      val format = if (config.pdf) "DOT/PDF" else "DOT"
      ProgressBar(s"dump CFG in a $format format", cfg.funcs).foreach(f => {
        val name = s"${CFG_LOG_DIR}/${f.name}"
        dumpFile(f.toDot, s"$name.dot")
        if (config.pdf) {
          // check whether dot is available
          if (isNormalExit("dot -V")) {
            try executeCmd(s"dot -Tpdf $name.dot -o $name.pdf") catch {
              case ex: Exception => println(s"[ERROR] $name: exception occur while converting to pdf")
            }
          } else println("Dot is not installed!")
        }
      })
    }

    cfg
  }

  def defaultConfig: BuildCFGConfig = BuildCFGConfig()
  val options: List[PhaseOption[BuildCFGConfig]] = List(
    ("dot", BoolOption(c => c.dot = true),
      "dump the cfg in a dot format."),
    ("pdf", BoolOption(c => { c.dot = true; c.pdf = true }),
      "dump the cfg in a dot and pdf format.")
  )
}

// BuildCFG config
case class BuildCFGConfig(
  var dot: Boolean = false,
  var pdf: Boolean = false
) extends Config
