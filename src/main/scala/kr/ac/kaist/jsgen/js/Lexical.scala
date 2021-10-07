package kr.ac.kaist.jsgen.js.ast

import kr.ac.kaist.jsgen.spec.algorithm._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.error.InvalidAST
import kr.ac.kaist.jsgen.spec.grammar._
import kr.ac.kaist.jsgen.util.{ Span, Pos }
import kr.ac.kaist.jsgen.util.Useful._
import io.circe._, io.circe.syntax._

object Lexical {
  def apply(data: Json): Lexical = AST(data) match {
    case Some(compressed) => Lexical(compressed)
    case None => error("invalid AST compressed form")
  }
  def apply(data: AST.Compressed): Lexical = {
    val AST.LexicalCompressed(kind, str) = data
    Lexical(kind, str)
  }
}

case class Lexical(kind: String, str: String) extends AST {
  def idx: Int = 0
  def k: Int = 0
  def parserParams: List[Boolean] = Nil
  def span: Span = Span()
  def fullList: List[(String, PureValue)] = Nil
  def maxK: Int = 0

  // name
  override def name: String = kind

  // pretty printer
  override def prettify: Json = toJson

  // to JSON format
  override def toJson: Json = Json.arr(
    Json.fromString(kind),
    Json.fromString(str),
  )

  // conversion to string
  override def toString: String = str
}
