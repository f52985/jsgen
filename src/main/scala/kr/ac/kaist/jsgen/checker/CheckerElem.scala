package kr.ac.kaist.jsgen.checker

import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.checker.Stringifier._

// type checker components
trait CheckerElem {
  // conversion to string
  override def toString: String = stringify(this)
}
