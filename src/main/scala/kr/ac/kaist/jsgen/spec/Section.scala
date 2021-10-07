package kr.ac.kaist.jsgen.spec

import org.jsoup.nodes._
import kr.ac.kaist.jsgen.util.Appender
import kr.ac.kaist.jsgen.util.Appender._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.extractor.SectionParser
import kr.ac.kaist.jsgen.spec.Parser

case class Section(id: String, subs: List[Section]) extends SpecElem
object Section extends Parser[Section] {
  def apply(elem: Element): Section = SectionParser(elem)

  implicit lazy val SectionApp: App[Section] = (app, section) => {
    app >> "[" >> section.id >> "] "
    app.listWrap(section.subs)
  }
}
