package kr.ac.kaist.jsgen.ir

import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util.Appender._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.js.ast._
import kr.ac.kaist.jsgen.spec.algorithm._
import kr.ac.kaist.jsgen.spec.grammar._
import kr.ac.kaist.jsgen.util.{ Span, Pos }
import scala.collection.mutable.{ Map => MMap }

class StringifierTinyTest extends IRTest {
  val name: String = "irStringifierTest"

  // test helper
  def test[T <: IRElem](desc: String)(cases: (T, String)*): Unit =
    check(desc, cases.foreach {
      case (given, expected) =>
        val result = given.toString
        if (result != expected) {
          println(s"$desc FAILED")
          println(s"result: $result")
          println(s"answer: $expected")
          assert(result == expected)
        }
    })

  // registration
  def init: Unit = {
    val irMapElems = List(
      EBool(true) -> EStr("true"),
      ENull -> EStr("null")
    )
    val sMapElems = "(true -> \"true\", null -> \"null\")"
    val irList = List(ENull, EAbsent)
    val sList = "(new [null, absent])"
    val irReturn = IReturn(EINum(4))
    val sReturn = "return 4i"
    val idList = List(Id("x"), Id("y"))
    val sIdList = "(x, y)"

    // Syntax
    test("Inst")(
      IExpr(EINum(4)) -> "4i",
      ILet(Id("x"), EINum(4)) -> "let x = 4i",
      IAssign(RefId(Id("x")), ENum(3.0)) -> "x = 3.0",
      IDelete(RefId(Id("ref"))) -> "delete ref",
      IAppend(EUndef, EList(irList)) -> s"append undefined -> $sList",
      IPrepend(EUndef, EList(irList)) -> s"prepend undefined -> $sList",
      irReturn -> sReturn,
      IThrow("SyntaxError") -> "throw SyntaxError",
      IIf(EBool(true), irReturn, IExpr(ENum(3.0))) ->
        s"if true $sReturn else 3.0",
      IWhile(EBool(false), irReturn) -> s"while false $sReturn",
      ISeq(List()) -> "{}",
      ISeq(List(irReturn, IExpr(ENull))) -> s"{\n  $sReturn\n  null\n}",
      IAssert(EBool(false)) -> "assert false",
      IPrint(EBool(false)) -> "print false",
      IApp(Id("x"), EStr("f"), irList) ->
        "app x = (\"f\" null absent)",
      IAccess(Id("x"), EStr("b"), ENum(3.0), Nil) ->
        "access x = (\"b\" 3.0)",
      IAccess(Id("x"), EStr("b"), ENum(3.0), List(EStr("x"), ENull)) ->
        "access x = (\"b\" 3.0 \"x\" null)",
      IClo(Id("x"), idList, idList, IExpr(EINum(4))) ->
        s"clo x = $sIdList[x, y] => 4i",
      ICont(Id("x"), idList, IExpr(EINum(4))) ->
        s"cont x = $sIdList [=>] 4i",
      IWithCont(Id("x"), idList, irReturn) ->
        s"withcont x $sIdList = $sReturn",
    )
    test("Expr")(
      ENum(3.0) -> "3.0",
      ENum(Double.PositiveInfinity) -> "Infinity",
      ENum(Double.NegativeInfinity) -> "-Infinity",
      ENum(Double.NaN) -> "NaN",
      EINum(4) -> "4i",
      EBigINum(1024) -> "1024n",
      EStr("hi") -> "\"hi\"",
      EBool(true) -> "true",
      EUndef -> "undefined",
      ENull -> "null",
      EAbsent -> "absent",
      EMap(Ty("T"), irMapElems) -> s"(new T$sMapElems)",
      EList(irList) -> sList,
      EPop(EList(irList), EINum(0)) -> s"(pop $sList 0i)",
      ERef(RefId(Id("x"))) -> "x",
      EUOp(ONeg, EINum(4)) -> "(- 4i)",
      EBOp(ODiv, ENum(3.0), ENum(7.0)) -> "(/ 3.0 7.0)",
      ETypeOf(EBool(false)) -> "(typeof false)",
      EIsCompletion(EINum(5)) -> "(is-completion 5i)",
      EIsInstanceOf(EBool(false), "instanceof") ->
        "(is-instance-of false instanceof)",
      EGetElems(EBool(false), "getelems") ->
        "(get-elems false getelems)",
      EGetSyntax(EAbsent) -> "(get-syntax absent)",
      EParseSyntax(EStr("code"), EStr("rule"), Nil)
        -> "(parse-syntax \"code\" \"rule\")",
      EParseSyntax(EStr("code"), EStr("rule"), List(true, false))
        -> "(parse-syntax \"code\" \"rule\" true false)",
      EConvert(ENull, CNumToBigInt, Nil) ->
        "(convert null num2bigint)",
      EConvert(EStr("4"), CStrToNum, irList) ->
        "(convert \"4\" str2num null absent)",
      EContains(EList(irList), ENull) -> s"(contains $sList null)",
      EReturnIfAbrupt(ENum(3.0), true) -> "[? 3.0]",
      EReturnIfAbrupt(ENum(3.0), false) -> "[! 3.0]",
      ECopy(EStr("obj")) -> "(copy-obj \"obj\")",
      EKeys(EStr("obj"), false) -> "(map-keys \"obj\")",
      EKeys(EStr("obj"), true) -> "(map-keys \"obj\" [int-sorted])",
      ENotSupported("hi") -> "??? \"hi\""
    )
    test("Ref")(
      RefId(Id("y")) -> "y",
      RefProp(RefId(Id("z")), EStr("w")) -> "z.w",
      RefProp(RefId(Id("x")), ENum(3.0)) -> "x[3.0]"
    )
    test("Ty")(Ty("T") -> "T")
    test("Id")(Id("x") -> "x")
    test("UOp")(
      ONeg -> "-",
      ONot -> "!",
      OBNot -> "~"
    )
    test("BOp")(
      OPlus -> "+",
      OSub -> "-",
      OMul -> "*",
      OPow -> "**",
      ODiv -> "/",
      OUMod -> "%%",
      OMod -> "%",
      OEq -> "=",
      OEqual -> "==",
      OAnd -> "&&",
      OOr -> "||",
      OXor -> "^^",
      OBAnd -> "&",
      OBOr -> "|",
      OBXOr -> "^",
      OLShift -> "<<",
      OLt -> "<",
      OURShift -> ">>>",
      OSRShift -> ">>"
    )
    test("COp")(
      CStrToNum -> "str2num",
      CStrToBigInt -> "str2bigint",
      CNumToStr -> "num2str",
      CNumToInt -> "num2int",
      CNumToBigInt -> "num2bigint",
      CBigIntToNum -> "bigint2num"
    )

    // State
    test("State")(
      State() -> """{
      |  context: {
      |    name: TOP_LEVEL
      |    return: RETURN
      |    cursor: [EMPTY]
      |    local-vars: {}
      |  }
      |  context-stack: []
      |  globals: {}
      |  heap: (SIZE = 0): {}
      |  filename: UNKNOWN
      |}""".stripMargin
    )
    test("Context")(
      Context() -> """{
      |  name: TOP_LEVEL
      |  return: RETURN
      |  cursor: [EMPTY]
      |  local-vars: {}
      |}""".stripMargin
    )
    test("Heap")(
      Heap(MMap(NamedAddr("namedaddr") -> IRSymbol(Num(3.0))), 1) -> """(SIZE = 1): {
      |  #namedaddr -> (Symbol 3.0)
      |}""".stripMargin
    )
    test("Obj")(
      IRSymbol(Str("const")) -> """(Symbol "const")""",
      IRMap(Ty("T"), MMap(Num(3.0) -> (Num(2.0), 4)), 1) -> """(TYPE = T) {
      |  3.0 -> 2.0
      |}""".stripMargin,
      IRList(Vector(Num(3.0))) -> "[3.0]",
      IRList(Vector(Num(3.0), INum(42))) -> "[3.0, 42i]",
      IRNotSupported("tyname", "desc") -> """(NotSupported "tyname" "desc")"""
    )
    test("Value")(
      Num(3.0) -> "3.0",
      INum(2) -> "2i",
      BigINum(BigInt("1920380182930189023")) -> "1920380182930189023n",
      Str("hello") -> """"hello"""",
      Bool(true) -> "true",
      Bool(false) -> "false",
      Undef -> "undefined",
      Null -> "null",
      Absent -> "absent"
    )
    test("Addr")(
      NamedAddr("GLOBAL") -> "#GLOBAL",
      DynamicAddr(3) -> "#3"
    )
    test("ASTVal")(
      ASTVal(PrimaryExpression0(List(), Span())) -> "☊[PrimaryExpression](this)"
    )
    test("Func")(
      Func(Algo(NormalHead("normalname", List()), "x", IExpr(EINum(4)), List())) ->
        "λ(normalname)",
      Func(Algo(MethodHead("base", "methodname", Param("p"), List()), "x", IExpr(EINum(4)), List())) ->
        "λ(base.methodname)",
      Func(Algo(SyntaxDirectedHead("lhsname", 0, 1, List(), "methodname", true, List(), true), "x", IExpr(EINum(4)), List())) ->
        "λ(lhsname[0,1].methodname)",
      Func(Algo(BuiltinHead(RefId(Id("id")), List()), "x", IExpr(EINum(4)), List())) ->
        "λ(GLOBAL.id)",
      Func(Algo(BuiltinHead(RefProp(RefId(Id("id")), ENum(3.0)), List()), "x", IExpr(EINum(4)), List())) ->
        "λ(GLOBAL.id[3.0])"
    )
    test("Clo")(
      Clo("clo", idList, MMap[Id, Value](Id("z") -> Num(3.0)), None) ->
        "clo:closure(x, y)[z -> 3.0] => ..."
    )
    test("Cont")(
      Cont(idList, Context(), List()) -> "TOP_LEVEL(x, y) [=>] ...",
    )
    test("RefValue")(
      RefValueId(Id("x")) -> "x",
      RefValueProp(NamedAddr("namedaddr"), Num(3.0)) -> "#namedaddr[3.0]",
      RefValueProp(NamedAddr("namedaddr"), Str("abc")) -> """#namedaddr["abc"]""",
      RefValueProp(Str("hello"), INum(3)) -> """"hello"[3i]"""
    )
  }
  init
}
