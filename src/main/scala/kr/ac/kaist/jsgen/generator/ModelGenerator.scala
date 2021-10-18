package kr.ac.kaist.jsgen.generator

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.spec.JsonProtocol._
import kr.ac.kaist.jsgen.spec.NativeHelper._
import kr.ac.kaist.jsgen.spec._
import kr.ac.kaist.jsgen.spec.grammar.Grammar
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util.Useful._

case class ModelGenerator(spec: ECMAScript, parser: Boolean) {
  val grammar = spec.grammar

  // generate model/VERSION in resource directory
  dumpSpec(spec, s"$VERSION_DIR/generated")

  // generate js/ast/*.scala in source code directory
  ASTGenerator(grammar)

  // generate js/Parser.scala in source code directory
  if (parser) ParserGenerator(grammar)

  // generate js/ASTWalker.scala in source code directory
  WalkerGenerator(grammar)

  // generate js/ASTDiff.scala in source code directory
  DiffGenerator(grammar)

  // generate js/ASTTransformer.scala in source code directory
  TransformerGenerator(grammar)
}
