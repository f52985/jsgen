package kr.ac.kaist.jsgen.checker

import kr.ac.kaist.jsgen.cfg
import kr.ac.kaist.jsgen.cfg.{ DotPrinter => _, _ }
import kr.ac.kaist.jsgen.util.Appender

trait DotPrinter extends cfg.DotPrinter {
  // colors
  val SHORTCUT = """"red""""
  val SELECTED = """"gray""""

  // normalize strings for view
  private val normPattern = """[-\[\](),\s~?"]""".r
  protected def norm(view: View): String = {
    normPattern.replaceAllIn(view.tys.map {
      case NormalT(RecordT(props)) => NormalT(NameT("Record"))
      case RecordT(props) => NameT("Record")
      case t => t
    }.toString, "")
  }
}
