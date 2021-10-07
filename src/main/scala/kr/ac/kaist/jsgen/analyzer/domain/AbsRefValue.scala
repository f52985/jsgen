package kr.ac.kaist.jsgen.analyzer.domain

import kr.ac.kaist.jsgen.analyzer.AnalyzerElem
import kr.ac.kaist.jsgen.ir._

// basic abstract reference values
sealed trait AbsRefValue extends AnalyzerElem
case class AbsRefId(id: Id) extends AbsRefValue
case class AbsRefProp(base: AbsValue, prop: AbsValue) extends AbsRefValue
