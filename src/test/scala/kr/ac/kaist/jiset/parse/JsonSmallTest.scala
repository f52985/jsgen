package kr.ac.kaist.jiset.parse

import kr.ac.kaist.jiset.JISETTest
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.parser.ECMAScriptParser
import kr.ac.kaist.jiset.spec.JsonProtocol._
import spray.json._
import org.scalatest._

class JsonSmallTest extends ParseTest {
  // registration
  def init: Unit = {
    check("ECMAScript (recent)", {
      val pre = JISETTest.specInputs("recent")
      val spec = ECMAScriptParser(pre, "", false, false)
      val json = spec.toJson
      val loaded = json.convertTo[ECMAScript]
      assert(spec == loaded)
    })
  }
  init
}
