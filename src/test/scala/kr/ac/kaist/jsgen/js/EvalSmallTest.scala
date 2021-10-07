package kr.ac.kaist.jsgen.js

import kr.ac.kaist.jsgen.JS_DIR
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.phase._
import kr.ac.kaist.jsgen.util.JvmUseful._

class EvalSmallTest extends JSTest {
  val name: String = "jsEvalTest"

  // registration
  def init: Unit = for (file <- walkTree(JS_DIR)) {
    val filename = file.getName
    if (jsFilter(filename)) check(filename, {
      val name = removedExt(filename)

      // evaluate a JS file
      val jsName = file.toString
      val st = evalTestFile(jsName)

      // evaluate the corresponding IR file
      val irName = js2ir(jsName)
      val program = Program.fromFile(irName)
      val newSt = st.copy(cursorGen = InstCursor)
      newSt.context.cursorOpt = InstCursor.from(program.insts)
      Interp(newSt)
    })
  }
  init
}
