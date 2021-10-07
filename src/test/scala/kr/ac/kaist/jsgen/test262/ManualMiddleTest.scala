package kr.ac.kaist.jsgen.test262

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.js.Test262
import kr.ac.kaist.jsgen.util.JvmUseful._

class ManualMiddleTest extends Test262Test {
  val name: String = "test262ManualTest"

  // filename for manual target names
  val filename = s"$BASE_DIR/tests/manual-test262"

  // logging with view information
  LOG = true
  VIEW = true

  // registration
  def init: Unit = check(name, {
    val manuals = readFile(filename).split(LINE_SEP).toSet
    val targets = Test262.config.normal.filter(manuals contains _.name)
    test262Test(targets, TestKind.Analyze)
  })
  init
}
