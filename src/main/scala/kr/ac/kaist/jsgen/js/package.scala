package kr.ac.kaist.jsgen

import kr.ac.kaist.jsgen.extractor.ECMAScriptParser
import kr.ac.kaist.jsgen.cfg.CFG
import kr.ac.kaist.jsgen.checker.CFGPartialModel
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.js.ast._
import kr.ac.kaist.jsgen.spec.JsonProtocol._
import kr.ac.kaist.jsgen.spec._
import kr.ac.kaist.jsgen.spec.algorithm._
import kr.ac.kaist.jsgen.util.Span
import kr.ac.kaist.jsgen.util.Useful._

package object js {
  // set current ECMAScript model
  def setSpec(spec: => ECMAScript): Unit =
    if (_spec.isEmpty) _spec = Some(spec)
  private var _spec: Option[ECMAScript] = None

  // current ECMAScript model
  lazy val spec: ECMAScript = _spec.get

  // current control-flow graph (CFG)
  lazy val cfg: CFG = new CFG(spec)

  // set current CFG partial model
  def setPartialModel(partialModel: => CFGPartialModel): Unit =
    if (_partialModel.isEmpty) _partialModel = Some(partialModel)
  private var _partialModel: Option[CFGPartialModel] = None

  // current CFG partial model
  lazy val partialModel: CFGPartialModel = _partialModel.get

  // json protocols
  lazy val cfgJsonProtocol = cfg.jsonProtocol
  lazy val checkerJsonProtocol = new checker.JsonProtocol(cfg)

  // ECMAScript components
  lazy val intrinsics: Set[String] = spec.intrinsics
  lazy val symbols: Set[String] = spec.symbols
  lazy val algoMap: Map[String, Algo] = spec.algoMap

  // default algorithm for `Contains` static semantics
  lazy val defaultContains: Algo = algoMap.getOrElse("Contains", {
    error("cannot find default `Contains` algorithm")
  })

  // conversion intrinsics to address
  val intrinsicRegex = "%([^%]+)%".r
  def intrinsicToAddr(name: String): Addr = {
    val newName = name
      .replaceAll("Function.prototype.prototype", ".prototype")
    NamedAddr(GLOBAL + "." + newName)
  }

  // constants
  val AGENT = "AGENT"
  val ALGORITHM = "ALGORITHM"
  val CONTEXT = "CONTEXT"
  val EXECUTION_STACK = "EXECUTION_STACK"
  val GLOBAL = "GLOBAL"
  val HOST_DEFINED = "HOST_DEFINED"
  val INTRINSICS = "INTRINSICS"
  val JOB_QUEUE = "JOB_QUEUE"
  val PRIMITIVE = "PRIMITIVE"
  val REALM = "REALM"
  val RESULT = "RESULT"
  val RETURN = "RETURN"
  val RET_CONT = "RET_CONT"
  val SCRIPT_BODY = "SCRIPT_BODY"
  val SYMBOL_REGISTRY = "SYMBOL_REGISTRY"
  val TOP_LEVEL = "TOP_LEVEL"
  val TYPED_ARRAY_INFO = "TYPED_ARRAY_INFO"

  // prefixes
  val SYMBOL_PREFIX = "SYMBOL_"

  // flatten statements
  def flattenStmtList(
    s: StatementList,
    list: List[StatementListItem] = Nil
  ): List[StatementListItem] = s match {
    case StatementList0(x0, _, _) => x0 :: list
    case StatementList1(x0, x1, _, _) => flattenStmtList(x0, x1 :: list)
  }
  def flattenStmt(s: Script): List[StatementListItem] = s match {
    case Script0(Some(ScriptBody0(stlist, _, _)), _, _) =>
      flattenStmtList(stlist)
    case _ => Nil
  }

  // merge statements to script
  def mergeStmtList(
    l: List[StatementListItem],
    params: List[Boolean] = List(false, false, false)
  ): Option[StatementList] = l match {
    case a :: rest => {
      val init: StatementList = StatementList0(a, params, a.span)
      val list = rest.foldLeft(init) {
        case (x, y) =>
          val span = Span(x.span.start, y.span.end)
          StatementList1(x, y, params, span)
      }
      Some(list)
    }
    case Nil => None
  }
  def mergeStmt(l: List[StatementListItem]): Script = {
    val params = List(false, false, false)
    val bodyOpt = mergeStmtList(l).map(l => ScriptBody0(l, params, l.span))
    val span = bodyOpt.fold(Span())(_.span)
    Script0(bodyOpt, params, span)
  }
}
