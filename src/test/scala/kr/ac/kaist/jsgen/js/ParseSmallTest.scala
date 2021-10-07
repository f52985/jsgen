package kr.ac.kaist.jsgen.js

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.js._
import kr.ac.kaist.jsgen.util.JvmUseful._

class ParseSmallTest extends JSTest {
  val name: String = "jsParseTest"

  // registration
  def init: Unit = for (file <- walkTree(JS_DIR)) {
    val filename = file.getName
    if (jsFilter(filename)) check(filename, {
      val jsName = file.toString
      val ast = parseFile(jsName)
      parseTest(ast)
    })
  }
  init
}
