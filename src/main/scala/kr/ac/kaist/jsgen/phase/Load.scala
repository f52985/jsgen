package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.js.{ Parser => JSParser, _ }
import kr.ac.kaist.jsgen.js.ast._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.spec.NativeHelper._
import kr.ac.kaist.jsgen.checker.NativeHelper._
import scala.io.Source

// Load phase
case object Load extends Phase[Script, LoadConfig, State] {
  val name = "load"
  val help = "loads a JavaScript AST to the initial IR states."

  def apply(
    script: Script,
    jsgenConfig: JSGenConfig,
    config: LoadConfig
  ): State = {
    val filename = getFirstFilename(jsgenConfig, "load")
    setSpec(loadSpec(s"$VERSION_DIR/generated"))
    if (PARTIAL) setPartialModel(loadPartialModel(s"$VERSION_DIR/partial.json"))
    Initialize(script, Some(filename), config.cursorGen)
  }

  def defaultConfig: LoadConfig = LoadConfig()
  val options: List[PhaseOption[LoadConfig]] = List(
    ("cursor", StrOption((c, s) => s match {
      case "inst" => c.cursorGen = InstCursor
      case "node" => c.cursorGen = NodeCursor
      case _ => c.cursorGen = NodeCursor
    }), "set the type of evaluation cursors (default: node)."),
  )
}

// Parse phase config
case class LoadConfig(
  var cursorGen: CursorGen[_ <: Cursor] = NodeCursor
) extends Config
