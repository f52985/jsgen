package kr.ac.kaist.jsgen.ir

// IR Properties
sealed trait RefValue extends IRElem
case class RefValueId(id: Id) extends RefValue
case class RefValueProp(base: Value, prop: PureValue) extends RefValue
