package kr.ac.kaist.jsgen.injector

import kr.ac.kaist.jsgen._
import kr.ac.kaist.jsgen.util._
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.error._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.ir.Parser._
import kr.ac.kaist.jsgen.js.ast.Script

case class Injector(fname: String, st: State) {
  // injected script
  lazy val result: String = {
    append(scriptStr)
    if (isAsync) startAsync
    handleVariable
    handleLet
    if (isAsync) endAsync
    getString
  }

  implicit def str2expr(str: String): Expr = Expr(str)

  // script
  private lazy val scriptStr = readFile(fname)

  // interpreter
  private lazy val interp = new Interp(st)

  //////////////////////////////////////////////////////////
  // Helper Functions
  //////////////////////////////////////////////////////////
  // add line
  private var sb = new StringBuilder
  private def append(str: String, comment: Boolean = false): Unit = {
    if (comment) sb.append("// ")
    sb.append(str).append(LINE_SEP)
  }
  private def getString: String = sb.toString

  // handle variables
  private def handleVariable: Unit = for (x <- createdVars) {
    interp.interp(s"$globalMap.$x.Value").escaped match {
      case s: SimpleValue => append(s"$$assert.sameValue($x, ${simple2code(s)});")
      case addr: Addr => handleObject(addr, x)
      case _ => warning;
    }
  }

  // handle lexical variables
  private def handleLet: Unit = for (x <- createdLets) {
    interp.interp(s"$lexRecord.$x.BoundValue").escaped match {
      case s: SimpleValue => append(s"$$assert.sameValue($x, ${simple2code(s)});")
      case addr: Addr => handleObject(addr, x)
      case _ => warning;
    }
  }

  // handle addresses
  private lazy val PREFIX_GLOBAL = "GLOBAL."
  private lazy val PREFIX_INTRINSIC = "INTRINSIC_"
  private def addrToName(addr: Addr): Option[String] = addr match {
    case a @ NamedAddr(name) if name.startsWith(PREFIX_GLOBAL) =>
      val str = name.substring(PREFIX_GLOBAL.length).replaceAll(s"#$PREFIX_GLOBAL", "")
      if (str.startsWith(PREFIX_INTRINSIC)) None
      else Some(str)
    case _ => None
  }
  private var handledObjects: Map[Addr, String] = (for {
    (addr, _) <- st.heap.map
    name <- addrToName(addr)
  } yield addr -> name).toMap
  private def handleObject(addr: Addr, path: String): Unit = {
    (addr, handledObjects.get(addr)) match {
      case (_, Some(origPath)) =>
        append(s"$$assert.sameValue($path, $origPath);")
      case (_: DynamicAddr, None) if addr != globalThis =>
        handledObjects += addr -> path
        //interp.addrName.get(addr).map(name => append(s"""$$algo.set($path, "$name")"""))
        st(addr) match {
          case (_: IRMap) =>
            handlePrototype(addr, path)
            handleExtensible(addr, path)
            handleCall(addr, path)
            handleConstruct(addr, path)
            handlePropKeys(addr, path)
            handleProperty(addr, path)
          case _ =>
        }
      case _ =>
    }
  }

  // handle [[Prototype]]
  private def handlePrototype(addr: Addr, path: String): Unit = {
    access(st, addr, Str("Prototype")) match {
      case (proto: Addr) => handleObject(proto, s"Object.getPrototypeOf($path)")
      case _ => warning
    }
  }

  // handle [[Extensible]]
  private def handleExtensible(addr: Addr, path: String): Unit = {
    access(st, addr, Str("Extensible")) match {
      case Bool(b) => append(s"$$assert.sameValue(Object.isExtensible($path), $b);")
      case _ => warning
    }
  }

  // handle [[Call]]
  private def handleCall(addr: Addr, path: String): Unit = {
    if (access(st, addr, Str("Call")) == Absent) {
      append(s"$$assert.notCallable($path);")
    } else append(s"$$assert.callable($path);")
  }

  // handle [[Construct]]
  private def handleConstruct(addr: Addr, path: String): Unit = {
    if (access(st, addr, Str("Construct")) == Absent) {
      append(s"$$assert.notConstructable($path);")
    } else append(s"$$assert.constructable($path);")
  }

  // handle property names
  private def handlePropKeys(addr: Addr, path: String): Unit = {
    val newSt = st.copy(globals = st.globals ++ Map(Id("input") -> addr))
    val newInterp = runInst(newSt, s"app result = (input.OwnPropertyKeys input)")
    val result = "result.Value"
    val len = newInterp.interp(s"$result.length").asInstanceOf[INum].long.toInt
    val array = (0 until len)
      .map(k => newInterp.interp(s"""$result[${k}i]"""))
      .flatMap(_ match {
        case Str(str) => Some(s"'$str'")
        case addr: Addr => addrToName(addr)
        case _ => None
      })
    if (array.length == len)
      append(s"$$assert.compareArray(Reflect.ownKeys($path), ${array.mkString("[", ", ", "]")}, $path);")
  }

  // handle properties
  private lazy val fields = List("Get", "Set", "Value", "Writable", "Enumerable", "Configurable")
  private def handleProperty(addr: Addr, path: String): Unit = {
    val subMap = access(st, addr, Str("SubMap"))
    for (p <- getKeys(subMap)) access(st, subMap, p) match {
      case addr: Addr => st(addr) match {
        case IRMap(Ty("DataProperty" | "AccessorProperty"), props, _) =>
          var set = Set[String]()
          val2code(p).map(propStr => {
            for {
              field <- fields
              (value, _) <- props.get(Str(field))
            } value.escaped match {
              case s: SimpleValue => set += s"${field.toLowerCase}: ${simple2code(s)}"
              case addr: Addr => field match {
                case "Value" => handleObject(addr, s"$path[$propStr]")
                case "Get" => handleObject(addr, s"Object.getOwnPropertyDescriptor($path, $propStr).get")
                case "Set" => handleObject(addr, s"Object.getOwnPropertyDescriptor($path, $propStr).set")
                case _ =>
              }
              case a => warning
            }
            val desc = set.mkString("{ ", ", ", "}")
            append(s"$$verifyProperty($path, $propStr, $desc);")
          })
        case x => warning
      }
      case _ => warning
    }
  }

  // handle async
  lazy val isAsync: Boolean = {
    scriptStr.contains("async") || scriptStr.contains("Promise")
  }
  private def startAsync: Unit = {
    append("$delay(() => {")
  }
  private def endAsync: Unit = {
    append("});")
  }

  // run instructions
  private def runInst(st: State, inst: String): Interp = {
    val interp = new Interp(st)
    interp.interp(Inst(inst), Nil)
    interp.fixpoint
    interp
  }

  // access properties
  private def access(st: State, base: Value, props: Value*): Value = {
    //case (base, p) => interp.getValue(st, interp.interp(base, p)(st)._1)._1

    props.foldLeft(base) {
      case (base, p: PureValue) => st(base, p)
      case _ => warning; base
    }
  }

  // get created variables
  private lazy val globalMap = "REALM.GlobalObject.SubMap"
  private lazy val globalThis = interp.interp(s"$globalMap.globalThis.Value")
  private lazy val createdVars: Set[String] = {
    val initial = getStrKeys(interp.interp("GLOBAL.SubMap"))
    val current = getStrKeys(interp.interp(globalMap))
    current -- initial
  }

  // get created lexical variables
  private lazy val lexRecord = "REALM.GlobalEnv.DeclarativeRecord.SubMap"
  private lazy val createdLets: Set[String] =
    getStrKeys(interp.interp(lexRecord))

  // get keys
  private def getStrKeys(value: Value): Set[String] =
    getKeys(value).collect { case Str(p) => p }
  private def getKeys(value: Value): Set[Value] = value match {
    case addr: Addr => st(addr) match {
      case (m: IRMap) => m.props.keySet.toSet
      case _ => warning; Set()
    }
    case _ => warning; Set()
  }

  // conversion to JS codes
  private def simple2code(s: SimpleValue): String = s match {
    case INum(n) => n.toString
    case s => s.toString
  }
  private def val2code(value: Value): Option[String] = value match {
    case s: SimpleValue => Some(simple2code(s))
    case addr: Addr => addrToName(addr) match {
      case Some(name) => Some(name)
      case None => warning; None
    }
    case x => warning; None
  }

  def warning: Unit = { println("warning"); ??? }
}
