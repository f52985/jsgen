package kr.ac.kaist.jsgen.test262

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.js.Test262

class PartialEvalLargeTest extends Test262Test {
  val name: String = "test262PartialEvalTest"

  // logging with view information / load partial model
  LOG = true
  VIEW = true
  PARTIAL = true

  // registration
  def init: Unit = check(name, {
    test262Test(Test262.config.normal, TestKind.EvalPartial)
  })
  init
}
