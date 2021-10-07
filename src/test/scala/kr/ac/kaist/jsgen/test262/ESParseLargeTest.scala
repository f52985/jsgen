package kr.ac.kaist.jsgen.test262

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.js._
import kr.ac.kaist.jsgen.phase.FilterMeta
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.util.JvmUseful._

class ESParseLargeTest extends Test262Test {
  val name: String = "test262ESParseTest"

  import Test262._

  // parser timeout
  val PARSE_TIMEOUT = 100 // second

  // progress bar
  val progress = ProgressBar("test262 esparse test", config.normal)

  // summary
  val summary = progress.summary

  // registration
  def init: Unit = check(name, {
    mkdir(logDir)

    cleanDir(ESPARSE_DIR)
    mkdir(ESPARSE_DIR)

    dumpFile(JSGenTest.spec.version, s"$logDir/ecma262-version")
    dumpFile(currentVersion(BASE_DIR), s"$logDir/jsgen-version")
    summary.fails.setPath(s"$logDir/esparse-fail.log")
    summary.passes.setPath(s"$logDir/esparse-pass.log")
    for (config <- progress) {
      val name = config.name
      val jsName = s"$TEST262_TEST_DIR/$name"
      val filename = name.split("/").last
      getError {
        timeout(esparseTest(jsName, filename), PARSE_TIMEOUT)
        summary.passes += name
      }.foreach(e => {
        summary.fails += name
      })
    }
    summary.close
    dumpFile(summary, s"$logDir/esparse-summary")
    if (summary.fail > 0) fail(s"${summary.fail} tests are failed.")
  })
  init
}
