package kr.ac.kaist.jsgen.analyzer

import kr.ac.kaist.jsgen.cfg
import kr.ac.kaist.jsgen.cfg.{ DotPrinter => _, _ }
import kr.ac.kaist.jsgen.util.Appender

trait DotPrinter extends cfg.DotPrinter {
  val SELECTED = """"gray""""

  // normalize strings for view
  private val normPattern = """[-:\[\](),\s~?"]""".r
  protected def norm(view: View): String = {
    normPattern.replaceAllIn(view.toString, "_")
  }
}
