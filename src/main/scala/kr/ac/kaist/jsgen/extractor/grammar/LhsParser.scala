package kr.ac.kaist.jsgen.extractor.grammar

import kr.ac.kaist.jsgen.spec.grammar._

// Lhs parsers
object LhsParser extends LhsParsers {
  def apply(str: String): Lhs = parseAll(lhs, str).get
}
trait LhsParsers extends Parsers {
  lazy val lhs: Parser[Lhs] = word ~ opt(params) <~ "[:]+".r ^^ {
    case n ~ None => Lhs(n, Nil)
    case n ~ Some(params) => Lhs(n, params)
  }
}
