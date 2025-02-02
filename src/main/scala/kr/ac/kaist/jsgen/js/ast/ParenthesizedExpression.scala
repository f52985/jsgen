package kr.ac.kaist.jsgen.js.ast

import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util.Span
import kr.ac.kaist.jsgen.util.Useful._
import io.circe._, io.circe.syntax._

trait ParenthesizedExpression extends AST { val kind: String = "ParenthesizedExpression" }

object ParenthesizedExpression {
  def apply(data: Json): ParenthesizedExpression = AST(data) match {
    case Some(compressed) => ParenthesizedExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ParenthesizedExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        ParenthesizedExpression0(x0, params, span)
    }
  }
}

case class ParenthesizedExpression0(x1: Expression, parserParams: List[Boolean], span: Span) extends ParenthesizedExpression {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("Expression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 )"
  }
}
