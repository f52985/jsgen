package kr.ac.kaist.jsgen.test262

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.js.Test262
import kr.ac.kaist.jsgen.util.JvmUseful._

class PartialManualMiddleTest extends Test262Test {
  val name: String = "test262PartialManualTest"

  // filename for manual target names
  val filename = s"$BASE_DIR/tests/manual-test262"

  // logging with view information / load partial model
  LOG = true
  VIEW = true
  PARTIAL = true

  // registration
  def init: Unit = check(name, {
    val manuals = readFile(filename).split(LINE_SEP).toSet
    val targets = Test262.config.normal.filter(manuals contains _.name)
    test262Test(targets, TestKind.EvalManualPartial)
  })
  init
}
