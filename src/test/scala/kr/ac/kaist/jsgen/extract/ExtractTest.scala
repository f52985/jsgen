package kr.ac.kaist.jsgen.extract

import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.extractor.algorithm.{ Compiler, TokenParser }
import kr.ac.kaist.jsgen.spec.JsonProtocol._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.spec.algorithm.Diff
import org.scalatest._

trait ExtractTest extends JSGenTest {
  val category: String = "extract"

  def diffTest(
    filename: String,
    result: IRElem,
    answer: IRElem,
    deep: Boolean = false
  ): Unit = {
    val diff = new Diff
    diff.deep = deep
    diff(result, answer) match {
      case Some(diff.Missing(missing)) =>
        println(s"==================================================")
        println(s"[$filename] MISS: $missing")
        println(s"--------------------------------------------------")
        val answerStr = answer.toString(line = true, asite = true)
        val resultStr = result.toString(line = true, asite = true)
        println(s"- result: $resultStr")
        println(s"- answer: $answerStr")
        fail(s"$answerStr is different with $resultStr")
      case None =>
    }
  }
}
