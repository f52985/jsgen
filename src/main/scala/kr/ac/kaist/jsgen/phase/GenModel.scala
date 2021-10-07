package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.generator._
import kr.ac.kaist.jsgen.spec._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.Useful._

// GenModel phase
case object GenModel extends Phase[ECMAScript, GenModelConfig, Unit] {
  val name: String = "gen-model"
  val help: String = "generates ECMAScript models."

  def apply(
    spec: ECMAScript,
    jsgenConfig: JSGenConfig,
    config: GenModelConfig
  ): Unit = time(s"generating models", {
    ModelGenerator(spec, config.parser)
  })

  def defaultConfig: GenModelConfig = GenModelConfig()
  val options: List[PhaseOption[GenModelConfig]] = List(
    ("parser", BoolOption(c => c.parser = true),
      "generate JavaScript parser."),
  )
}

// GenModel phase config
case class GenModelConfig(
  var parser: Boolean = false
) extends Config
