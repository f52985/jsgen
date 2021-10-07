package kr.ac.kaist.jsgen.extract

import kr.ac.kaist.jsgen.JSGenTest
import kr.ac.kaist.jsgen.spec.ECMAScript
import kr.ac.kaist.jsgen.extractor.ECMAScriptParser
import kr.ac.kaist.jsgen.spec.JsonProtocol._
import org.scalatest._
import kr.ac.kaist.jsgen.spec.algorithm.Diff
import io.circe._, io.circe.syntax._

class JsonSmallTest extends ExtractTest {
  val name: String = "extractJsonTest"

  // registration
  def init: Unit = {
    check("ECMAScript (recent)", {
      val spec = JSGenTest.spec
      val json = spec.asJson
      for (loaded <- json.as[ECMAScript]) {
        val diff = new Diff
        diff.deep = true
        assert(spec == loaded)
        (spec.algos zip loaded.algos).foreach {
          case (l, r) => {
            assert(diff.compare(l.rawBody, r.rawBody))
          }
        }
      }
    })
  }
  init
}
