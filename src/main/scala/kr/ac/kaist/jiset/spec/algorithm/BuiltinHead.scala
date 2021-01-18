package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.ires.ir

// built-in algorithm heads
case class BuiltinHead(
    ref: ir.Ref,
    origParams: List[String]
) extends Head {
  // name from base and fields
  val name: String = ir.beautify(ref)

  // fixed parameters for built-in algorithms
  val params: List[String] = Head.BUILTIN_PARAMS
}
