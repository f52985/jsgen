package kr.ac.kaist.jsgen.phase

import java.io.File
import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.extractor.algorithm.Compiler
import kr.ac.kaist.jsgen.spec.algorithm._
import kr.ac.kaist.jsgen.spec.JsonProtocol._
import kr.ac.kaist.jsgen.spec._
import kr.ac.kaist.jsgen.util.UIdGen
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.extractor.ECMAScriptParser
import kr.ac.kaist.jsgen.extractor.algorithm.TokenParser
import org.jsoup.nodes._

// GenTest phase
case object GenTest extends Phase[Unit, GenTestConfig, Unit] {
  val name: String = "gen-test"
  val help: String = "generates tests with the current implementation as the oracle."

  type Extracted = ((Array[String], Document, Region), ECMAScript)
  def apply(
    non: Unit,
    jsgenConfig: JSGenConfig,
    config: GenTestConfig
  ): Unit = {
    TEST_MODE = true
    val (_, extracted) = time(s"extract ECMAScript ($VERSION)", {
      val input = ECMAScriptParser.preprocess(VERSION)
      val spec = ECMAScriptParser(VERSION, input, "", false)
      (input, spec)
    })
    genGrammarTest(extracted)
  }

  // util
  val json2ir = changeExt("json", "ir")

  // generate grammar test
  def genGrammarTest(extracted: Extracted): Unit =
    time("generate grammar tests", {
      mkdir(GRAMMAR_DIR)
      val (_, spec) = extracted
      val filename = s"$GRAMMAR_DIR/$VERSION.grammar"
      dumpFile(spec.grammar, filename)
    })

  def defaultConfig: GenTestConfig = GenTestConfig()
  val options: List[PhaseOption[GenTestConfig]] = Nil
}

// GenTest phase config
case class GenTestConfig() extends Config
