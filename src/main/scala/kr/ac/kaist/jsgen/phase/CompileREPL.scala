package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.extractor.ECMAScriptParser
import kr.ac.kaist.jsgen.extractor.algorithm.{ CompileREPL => REPL }
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util._

// CompileREPL phase
case object CompileREPL extends Phase[Unit, CompileREPLConfig, Unit] {
  val name = "compile-repl"
  val help = "performs REPL for printing compile result of particular step."

  def apply(
    unit: Unit,
    jsgenConfig: JSGenConfig,
    config: CompileREPLConfig
  ): Unit = {
    val CompileREPLConfig(versionOpt, detail) = config
    val version = versionOpt.getOrElse(VERSION)
    println(s"version: $version")

    implicit val (_, (lines, document, region)) =
      time("preprocess", ECMAScriptParser.preprocess(version))
    implicit val (_, (grammar, _)) =
      time("parse ECMAScript grammar", ECMAScriptParser.parseGrammar(version))
    val (_, (secIds, _)) =
      time("parse algorithm heads", ECMAScriptParser.parseHeads())

    REPL.run(version, secIds)
  }

  def defaultConfig: CompileREPLConfig = CompileREPLConfig()
  val options: List[PhaseOption[CompileREPLConfig]] = List(
    ("version", StrOption((c, s) => c.version = Some(s)),
      "set the git version of ecma262."),
    ("detail", BoolOption(c => c.detail = true),
      "print log.")
  )
}

// CompileREPL phase config
case class CompileREPLConfig(
  var version: Option[String] = None,
  var detail: Boolean = false
) extends Config
