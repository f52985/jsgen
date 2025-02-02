package kr.ac.kaist.jsgen.checker

import kr.ac.kaist.jsgen.spec.algorithm.{ NormalHead, MethodHead }

// modeling
object Global {
  // lookup global variables
  def apply(x: String): AbsType = globalVars.getOrElse(x, AAbsent)

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  private lazy val globalVars: Map[String, AbsType] = (
    globalMethods ++
    getPredefs ++
    Map(
      "ALGORITHM" -> NameT("ALGORITHM"),
      "CONTEXT" -> NameT("ExecutionContext"),
      "EXECUTION_STACK" -> ListT(NameT("ExecutionContext")),
      "INTRINSICS" -> MapT(NameT("OrdinaryObject")),
      "PRIMITIVE" -> NameT("PrimitiveMethod"),
      "AGENT" -> NameT("AgentRecord"),
      "REALM" -> NameT("RealmRecord"),
      "Object" -> "Object",
      "String" -> "String",
      "Symbol" -> "Symbol",
      "Undefined" -> "Undefined",
      "Null" -> "Null",
      "Boolean" -> "Boolean",
      "Reference" -> "Reference",
      "Number" -> "Number",
      "BigInt" -> "BigInt",
    )
  )

  // get global methods
  private def globalMethods: Map[String, AbsType] = (for {
    func <- cfg.funcs
    head <- func.headOption
    name <- head match {
      case NormalHead(name, _) => Some(name)
      case MethodHead(base, methodName, _, _) => Some(s"${base}DOT${methodName}")
      case _ => None
    }
  } yield name -> FuncT(func.uid).abs).toMap

  // get pre-defined global variables
  private def getPredefs: Map[String, AbsType] = {
    var env = Map[String, AbsType]()
    val (intrinsics, symbols, asts) = cfg.getNames
    for (x <- intrinsics) env += x -> NameT("OrdinaryObject")
    for (x <- symbols) env += x -> SymbolT
    for (x <- asts) env += s"AST_$x" -> AstT(x)
    env
  }
}
