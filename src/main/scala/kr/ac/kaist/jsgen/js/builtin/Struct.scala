package kr.ac.kaist.jsgen.js.builtin

import kr.ac.kaist.jsgen.ir._

// builtin model structure
case class Struct(
  typeName: String,
  imap: IMap = IMap(),
  nmap: NMap = NMap()
) {
  // conversion to map structure
  def toMap(name: String): Map[Addr, Obj] = {
    var m = Map[Addr, Obj]()
    m += imap.toPair(name, typeName)
    m += nmap.toPair(name)
    m ++= nmap.getDescs(name)
    m
  }
}
