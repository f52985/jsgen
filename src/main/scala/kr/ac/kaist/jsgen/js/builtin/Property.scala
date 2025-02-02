package kr.ac.kaist.jsgen.js.builtin

import kr.ac.kaist.jsgen.ir._

// properties
sealed trait Property

// data properties
case class DataProperty(
  value: PureValue,
  writable: Boolean,
  enumerable: Boolean,
  configurable: Boolean
) extends Property

// accessor properties
case class AccessorProperty(
  get: PureValue,
  set: PureValue,
  enumerable: Boolean,
  configurable: Boolean
) extends Property
