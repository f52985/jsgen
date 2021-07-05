package kr.ac.kaist.jiset.test262

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.phase.FilterMeta
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._

class ParseLargeTest extends Test262Test {
  val name: String = "test262ParseTest"

  import Test262._

  // parser timeout
  val PARSE_TIMEOUT = 100 // second

  // progress bar
  val progress = ProgressBar("test262 parse test", config.normal)

  // summary
  val summary = progress.summary

  // registration
  def init: Unit = check(name, {
    mkdir(logDir)
    summary.fails.setPath(s"$logDir/eval-fail.log")
    summary.passes.setPath(s"$logDir/eval-pass.log")
    for (config <- progress) {
      val name = config.name
      val jsName = s"$TEST262_TEST_DIR/$name"
      getError {
        timeout(parseTest(parseFile(jsName)), PARSE_TIMEOUT)
        summary.passes += name
      }.foreach(e => {
        summary.fails += name
      })
    }
    summary.close
    dumpFile(summary, s"$logDir/parse-summary")
    if (summary.fail > 0) fail(s"${summary.fail} tests are failed.")
  })
  init
}
