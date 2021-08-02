package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.cfg._

package object domain {
  // abstract states
  val AbsState = BasicState
  type AbsState = AbsState.Elem

  // abstract heaps
  val AbsHeap = BasicHeap
  type AbsHeap = AbsHeap.Elem

  // abstract objects
  val AbsObj = BasicObj
  type AbsObj = AbsObj.Elem

  // abstract values
  val AbsValue = BasicValue
  type AbsValue = AbsValue.Elem

  // abstract locations
  val AbsLoc = new SetDomain[Loc]
  type AbsLoc = AbsLoc.Elem

  // abstract functions
  val AbsFunc = new SetDomain[Function]
  type AbsFunc = AbsFunc.Elem

  // abstract closures
  val AbsClo = new FlatDomain[AClo]
  type AbsClo = AbsClo.Elem

  // abstract continuations
  val AbsCont = new FlatDomain[ACont]
  type AbsCont = AbsCont.Elem

  // abstract simple values
  val AbsSimple = BasicSimple
  type AbsSimple = AbsSimple.Elem

  // abstract AST values
  val AbsAST = new FlatDomain[AST]
  type AbsAST = AbsAST.Elem

  // abstract floating-point number values
  val AbsNum = new FlatDomain[Num]
  type AbsNum = AbsNum.Elem

  // abstract integers
  val AbsInt = new FlatDomain[Long]
  type AbsInt = AbsInt.Elem

  // abstract big integers
  val AbsBigInt = new FlatDomain[BigInt]
  type AbsBigInt = AbsBigInt.Elem

  // abstract strings
  val AbsStr = new FlatDomain[String]
  type AbsStr = AbsStr.Elem

  // abstract booleans
  val AbsBool = new FlatDomain[Boolean]
  type AbsBool = AbsBool.Elem

  // abstract undefined
  val AbsUndef = new SimpleDomain(Undef)
  type AbsUndef = AbsUndef.Elem

  // abstract null
  val AbsNull = new SimpleDomain(Null)
  type AbsNull = AbsNull.Elem

  // abstract absent
  val AbsAbsent = new SimpleDomain(Absent)
  type AbsAbsent = AbsAbsent.Elem
}
