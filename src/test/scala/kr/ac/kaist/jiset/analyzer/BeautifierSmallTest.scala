package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._

class BeautifierSmallTest extends AnalyzerTest {
  def test[T](desc: String)(cases: (T, String)*)(
    implicit
    tApp: App[T]
  ): Unit = check(desc, cases.foreach {
    case (given, expected) =>
      val result = beautify(given)
      if (result != expected) {
        println(s"FAILED: $result != $expected")
        assert(result == expected)
      }
  })

  // registration
  def init: Unit = {
    test("Primitive Values")(
      Num(42.34) -> "42.34",
      INum(23) -> "23i",
      BigINum(BigInt(2).pow(100)) -> "1267650600228229401496703205376n",
      Str("hello") -> "\"hello\"",
      Bool(true) -> s"true",
      Bool(false) -> s"false",
      Undef -> "undefined",
      Null -> "null",
      Absent -> "absent",
    )

    test("AST values")(ASTVal("Literal") -> "☊(Literal)")

    test("Addresses")(
      NamedAddr("Global") -> "#Global",
      DynamicAddr(432) -> "#432",
    )

    test("Abstract Values")(
      AbsValue(42.34, BigInt(24), true) -> "42.34 | 24n | true",
      AbsValue(123, "abc", Undef, Null, Absent) -> "123i | \"abc\" | undef | null | ?",
      AbsValue(1.2, 2.3, 3, 4, BigInt(2), BigInt(3)) -> "num | int | bigint",
      AbsValue("a", "b", true, false) -> "str | bool",
      AbsValue(42, NamedAddr("Global"), DynamicAddr(432)) -> "(#Global | #432) | 42i",
      (AbsValue(true, Cont()) ⊔ AbsClo.Top) -> "λ | κ | true",
      AbsValue(ASTVal("Literal"), ASTVal("Identifier")) -> "(☊(Literal) | ☊(Identifier))",
    )

    test("Abstract Objects")(
      AbsObj(SymbolObj("has"), SymbolObj("get")) -> "@(has | get)",
      AbsObj(MapObj("x" -> true, "y" -> 2), MapObj("x" -> "a", "z" -> Null)) -> """{
      |  x -> ! "a" | true
      |  y -> ? 2i
      |  z -> ? null
      |}""".stripMargin,
      AbsObj(ListObj(Undef, true, 42)) -> "[undef, true, 42i]",
      AbsObj(SymbolObj("has"), MapObj(), ListObj()) -> "@has | {} | []",
    )

    val heap = AbsHeap(Heap(
      NamedAddr("Global") -> SymbolObj("has"),
      DynamicAddr(42) -> MapObj(),
    ))
    test("Abstract Heaps")(
      heap -> """{
      |  #Global -> @has
      |  #42 -> {}
      |}""".stripMargin,
    )

    val env = AbsEnv(Env(
      "x" -> 42,
      "y" -> true,
    ), Env(
      "x" -> 42,
      "z" -> Null,
    ))
    test("Abstract Environments")(
      env -> """{
      |  x -> ! 42i
      |  y -> ? true
      |  z -> ? null
      |}""".stripMargin,
    )

    val ctxt = AbsCtxt.Elem(
      globals = env,
      locals = env,
      retVal = AbsValue(Null, true)
    )
    test("Abstract Contexts")(
      ctxt -> """{
      |  globals: {
      |    x -> ! 42i
      |    y -> ? true
      |    z -> ? null
      |  },
      |  locals: {
      |    x -> ! 42i
      |    y -> ? true
      |    z -> ? null
      |  },
      |  return: true | null
      |}""".stripMargin,
    )

    val st = AbsState.Elem(ctxt, heap)
    test("Abstract State")(
      st -> """{
      |  context: {
      |    globals: {
      |      x -> ! 42i
      |      y -> ? true
      |      z -> ? null
      |    },
      |    locals: {
      |      x -> ! 42i
      |      y -> ? true
      |      z -> ? null
      |    },
      |    return: true | null
      |  },
      |  heap: {
      |    #Global -> @has
      |    #42 -> {}
      |  }
      |}""".stripMargin,
    )
  }
  init
}
