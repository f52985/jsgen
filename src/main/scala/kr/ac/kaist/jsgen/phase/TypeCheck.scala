package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen.JSGenConfig
import kr.ac.kaist.jsgen.cfg._
import kr.ac.kaist.jsgen.checker._
import kr.ac.kaist.jsgen.checker.NativeHelper._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen._

// TypeCheck phase
case object TypeCheck extends Phase[CFG, TypeCheckConfig, AbsSemantics] {
  val name = "type-check"
  val help = "performs type checks for specifications."

  def apply(
    cfg: CFG,
    jsgenConfig: JSGenConfig,
    config: TypeCheckConfig
  ): AbsSemantics = {
    // load abstract semantics
    val givenSem = config.load.map(dir => {
      val (_, sem) = time(
        s"loading abstract semantics from $dir",
        loadSem(dir)
      )
      sem
    })

    // perform type check
    performTypeCheck(cfg, givenSem)

    // dump partial models
    PARTIAL_MODEL.map(dirname => time(s"dump models to $dirname", {
      mkdir(dirname)
      for (algo <- cfg.spec.algos) {
        val name = algo.name
        val filename = s"$dirname/$name.model"
        dumpFile(PartialModel.getString(algo), filename)
      }
    }))

    // dump abstract semantics
    config.dump.map(dirname => time("dump abstract semantics", {
      dumpSem(sem, dirname)
    }))

    // result
    sem
  }

  def defaultConfig: TypeCheckConfig = TypeCheckConfig()
  val options: List[PhaseOption[TypeCheckConfig]] = List(
    ("dot", BoolOption(c => DOT = true),
      "dump CFG in a dot format."),
    ("pdf", BoolOption(c => { DOT = true; PDF = true }),
      "dump CFG in a dot and pdf format."),
    ("no-prune", BoolOption(c => PRUNE = false),
      "no abstract state pruning."),
    ("insens", BoolOption(c => USE_VIEW = false),
      "not use type sensitivity for parameters."),
    ("check-bug", BoolOption(c => CHECK_BUG = true),
      "check alarms."),
    ("target", StrOption((c, s) => TARGET = Some(s)),
      "set the target of type checks."),
    ("repl", BoolOption(c => USE_REPL = true),
      "use REPL for type checks."),
    ("partial-model", StrOption((c, s) => PARTIAL_MODEL = Some(s)),
      "dump partial models using type checking results."),
    ("load", StrOption((c, s) => c.load = Some(s)),
      "load abstract semantics from a directory."),
    ("dump", StrOption((c, s) => c.dump = Some(s)),
      "dump abstract semantics to a directory."),
  )
}

// TypeCheck phase config
case class TypeCheckConfig(
  var load: Option[String] = None,
  var dump: Option[String] = None
) extends Config
