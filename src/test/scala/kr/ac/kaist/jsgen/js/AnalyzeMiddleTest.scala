package kr.ac.kaist.jsgen.js

import kr.ac.kaist.jsgen.JS_DIR
import kr.ac.kaist.jsgen.analyzer._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.phase._
import kr.ac.kaist.jsgen.util.JvmUseful._

class AnalyzeMiddleTest extends JSTest {
  val name: String = "jsAnalyzeTest"

  // more loop depth
  LOOP_DEPTH = 50

  // registration
  def init: Unit = for (file <- walkTree(JS_DIR)) {
    val filename = file.getName
    if (jsFilter(filename)) check(filename, {
      val name = removedExt(filename)
      // analyze a JS file
      val jsName = file.toString
      analyzeFile(jsName, 1)
    })
  }
  init
}
