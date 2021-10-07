package kr.ac.kaist.jsgen.checker

import kr.ac.kaist.jsgen.util.Useful._

// check variables in env, list/map objects in heap has bottoms
object CheckBottoms {
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint[_]) => this(sem(np))
    case rp: ReturnPoint => this(sem(rp), "a return value")
  }

  def apply(st: AbsState): Unit =
    for ((x, aty) <- st.map) this(aty, s"variable $x")

  def apply(aty: AbsType, msg: String): Unit =
    if (aty.isBottom) typeWarning(s"Bottom result found: $msg")
}
