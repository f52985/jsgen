package kr.ac.kaist.jsgen.spec

import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.spec.Stringifier._

// specification components
trait SpecElem {
  // conversion to string
  override def toString: String = stringify(this)
}
