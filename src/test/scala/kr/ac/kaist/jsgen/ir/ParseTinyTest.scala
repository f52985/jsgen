package kr.ac.kaist.jsgen.ir

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util.JvmUseful._

class ParseTinyTest extends IRTest {
  val name: String = "irParseTest"

  // registration
  def init: Unit = for (file <- walkTree(IR_DIR)) {
    val filename = file.getName
    if (irFilter(filename)) check(filename, {
      val name = file.toString
      irParseTestFile(name)
    })
  }
  init
}
