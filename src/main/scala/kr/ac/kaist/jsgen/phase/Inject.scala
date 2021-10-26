package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.injector.Injector
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.Useful._
import kr.ac.kaist.jsgen.ir.State

// Inject phase
case object Inject extends Phase[State, InjectConfig, String] {
  val name = "inject"
  val help = "constructs tests by injecting semantics assertions to given JavaScript program."

  def apply(
    st: State,
    jsgenConfig: JSGenConfig,
    config: InjectConfig
  ): String = Injector(st).result

  /*
  jsgenConfig.fileNames.headOption match {
    case Some(filename) =>
      val parseResult = parse(Script(Nil), fileReader(filename))
      if (parseResult.successful)
        //dumpFile(Injector(parseResult.get).result, filename)
        println(Injector(parseResult.get).result)
    case None =>
      println("--------------- inject -----------------")
      println("injecting assertions...")
      mkdir(TESTS_DIR)

      var count = 0
      var total = 0
      for {
        file <- walkTree(PROGRAMS_DIR)
        name = file.getName
        filename = file.toString if jsFilter(filename)
        parseResult = parse(Script(Nil), fileReader(filename)) if parseResult.successful
        script = parseResult.get
      } try {
        if (DETAIL) print(f"[$name%10s] ")
        val injector = Injector(script)
        val injected = injector.result
        total += 1
        if (injector.isAsync) count += 1
        dumpFile(injected, s"$TESTS_DIR/$name")

        // dump touched
        val visited = injector.visited
        val toJsonExt = changeExt("js", "json")
        dumpJson(visited.touchedAlgos, s"$TOUCHED_ALGO_DIR/${toJsonExt(name)}")
        dumpJson(visited.instCovered, s"$TOUCHED_INST_DIR/${toJsonExt(name)}")
        if (DETAIL) println(s"Success")
      } catch {
        case e: Throwable => {
          val msg = e.getMessage()
          if (DETAIL) println(s"Failed: $msg")
          dumpFile(Map(
            "message" -> msg,
            "stacktrace" -> e.getStackTrace().mkString(LINE_SEP)
          ).toJson, s"$INJECT_EXC_DIR/$name.json")
        }
      }
      println(s"dumped generated tests to $TESTS_DIR.")
  }
  */

  def defaultConfig: InjectConfig = InjectConfig()
  val options: List[PhaseOption[InjectConfig]] = Nil
}

// Inject phase config
case class InjectConfig() extends Config
