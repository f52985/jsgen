package kr.ac.kaist.jsgen.ir

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util.JvmUseful._

class EvalTinyTest extends IRTest {
  val name: String = "irEvalTest"

  // registration
  def init: Unit = for (file <- walkTree(IR_DIR)) {
    val filename = file.getName
    if (irFilter(filename)) check(filename, {
      val irName = file.toString
      irEvalFile(irName)
    })
  }
  init
}
