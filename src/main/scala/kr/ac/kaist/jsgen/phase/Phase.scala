package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen.JSGenConfig
import kr.ac.kaist.jsgen.util.ArgParser

trait Phase[Input, PhaseConfig <: Config, Output] {
  val name: String
  val help: String
  def apply(
    in: Input,
    jsgenConfig: JSGenConfig,
    config: PhaseConfig = defaultConfig
  ): Output
  def defaultConfig: PhaseConfig
  val options: List[PhaseOption[PhaseConfig]]

  def getRunner(
    parser: ArgParser
  ): (Input, JSGenConfig) => Output = {
    val config = defaultConfig
    parser.addRule(config, name, options)
    (in, jsgenConfig) => {
      if (!jsgenConfig.silent) {
        println(s"========================================")
        println(s" $name phase")
        println(s"----------------------------------------")
      }
      apply(in, jsgenConfig, config)
    }
  }

  def getOptShapes: List[String] = options.map {
    case (opt, kind, _) => s"-$name:${opt}${kind.postfix}"
  }
  def getOptDescs: List[(String, String)] = options.map {
    case (opt, kind, desc) => (s"-$name:${opt}${kind.postfix}", desc)
  }
}

trait Config
