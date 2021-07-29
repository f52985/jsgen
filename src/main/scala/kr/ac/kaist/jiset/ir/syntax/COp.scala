package kr.ac.kaist.jiset.ir

sealed trait COp extends IRComponent
object COp extends Parser[COp]
case object CStrToNum extends COp
case object CStrToBigInt extends COp
case object CNumToStr extends COp
case object CNumToInt extends COp
case object CNumToBigInt extends COp
case object CBigIntToNum extends COp
