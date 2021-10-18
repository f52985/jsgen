package kr.ac.kaist.jsgen.generator

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.spec.grammar._
import kr.ac.kaist.jsgen.spec.grammar.token._
import kr.ac.kaist.jsgen.util.JvmUseful._

case class TransformerGenerator(grammar: Grammar) {
  val Grammar(lexProds, prods) = grammar
  val lexNames = lexProds.map(_.lhs.name).toSet

  val nf = getPrintWriter(s"$SRC_DIR/js/ASTTransformer.scala")
  generate
  nf.close()

  private def generate: Unit = {
    nf.println(s"""package $PACKAGE_NAME.js""")
    nf.println
    nf.println(s"""import $PACKAGE_NAME.js.ast._""")
    nf.println
    nf.println(s"""trait ASTTransformer {""")
    nf.println(s"""  def transform[T](opt: Option[T], t: T => T): Option[T] = opt.map(t)""")
    nf.println(s"""  def transform(lex: Lexical): Lexical = lex""")
    nf.println
    prods.foreach(genTransformer)
    nf.println(s"""}""")
  }

  private def genTransformer(prod: Production): Unit = {
    val Production(lhs, rhsList) = prod
    val Lhs(name, rawParams) = lhs
    nf.println(s"""  def transform(ast: $name): $name = ast match {""")
    for ((rhs, k) <- rhsList.zipWithIndex) {
      val xs = for {
        (token, i) <- rhs.tokens.zipWithIndex
        (name, opt) <- getInfo(token)
      } yield (s"x$i", name, opt)
      val argsStr = (xs.map(_._1) ++ List("params", "span")).mkString(", ")
      if (xs.isEmpty) {
        nf.println(s"""    case $name$k($argsStr) => ast""")
      } else {
        nf.println(s"""    case $name$k($argsStr) =>""")
        val args = xs.map {
          case (x, name, true) => s"transform[$name]($x, transform)"
          case (x, _, false) => s"transform($x)"
        }.mkString(", ")
        nf.println(s"""      $name$k($args, params, span)""")
      }
    }
    nf.println(s"""  }""")
  }

  private def getInfo(token: Token): Option[(String, Boolean)] = token match {
    case NonTerminal(name, _, optional) => Some((name, optional))
    case ButNot(base, _) => getInfo(base)
    case _ => None
  }
}
