package kr.ac.kaist.jsgen.extract

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.spec.grammar._
import kr.ac.kaist.jsgen.util.JvmUseful._
import org.scalatest._

class GrammarSmallTest extends ExtractTest {
  val name: String = "extractGrammarTest"

  // registration
  def init: Unit = check(VERSION, {
    val filename = s"$GRAMMAR_DIR/$VERSION.grammar"
    val answer = readFile(filename)
    val grammar = JSGenTest.spec.grammar
    assert(answer == grammar.toString)
  })
  init
}
