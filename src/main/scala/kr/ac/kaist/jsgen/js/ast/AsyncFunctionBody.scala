package kr.ac.kaist.jsgen.js.ast

import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util.Span
import kr.ac.kaist.jsgen.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncFunctionBody extends AST { val kind: String = "AsyncFunctionBody" }

object AsyncFunctionBody {
  def apply(data: Json): AsyncFunctionBody = AST(data) match {
    case Some(compressed) => AsyncFunctionBody(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncFunctionBody = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(FunctionBody(_)).get
        AsyncFunctionBody0(x0, params, span)
    }
  }
}

case class AsyncFunctionBody0(x0: FunctionBody, parserParams: List[Boolean], span: Span) extends AsyncFunctionBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FunctionBody", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
