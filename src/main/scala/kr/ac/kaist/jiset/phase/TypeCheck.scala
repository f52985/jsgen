package kr.ac.kaist.jiset.phase

import kr.ac.kaist.jiset.JISETConfig
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.NativeHelper._
import kr.ac.kaist.jiset.analyzer.JsonProtocol._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset._

// TypeCheck phase
case object TypeCheck extends Phase[CFG, TypeCheckConfig, AbsSemantics] {
  val name = "type-check"
  val help = "performs type analysis for specifications."

  def apply(
    cfg: CFG,
    jisetConfig: JISETConfig,
    config: TypeCheckConfig
  ): AbsSemantics = {
    // perform type analysis
    performTypeAnalysis(cfg, config.load)

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
      "dump the analyzed cfg in a dot format."),
    ("pdf", BoolOption(c => { DOT = true; PDF = true }),
      "dump the analyze cfg in a dot and pdf format."),
    ("no-prune", BoolOption(c => PRUNE = false),
      "no abstract state pruning."),
    ("insens", BoolOption(c => USE_VIEW = false),
      "not use type sensitivity for parameters."),
    ("check-bug", BoolOption(c => CHECK_BUG = true),
      "check alarms."),
    ("target", StrOption((c, s) => TARGET = Some(s)),
      "set the target of analysis."),
    ("repl", BoolOption(c => REPL = true),
      "use analyze-repl."),
    ("partial-model", StrOption((c, s) => PARTIAL_MODEL = Some(s)),
      "dump partial models using type analysis results."),
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
