package kr.ac.kaist.jsgen.test262

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.js.Test262

class EvalLargeTest extends Test262Test {
  val name: String = "test262Test"

  // logging with view information
  LOG = true
  VIEW = true

  // registration
  def init: Unit = check(name, {
    test262Test(Test262.config.normal, TestKind.Eval)
  })
  init
}
