package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.UId

// CFG functions
case class Function(
  algo: Algo,
  entry: Entry,
  exit: Exit,
  nodes: Set[Node],
  forwards: Map[Node, Set[(Edge, Node)]]
) extends UId {
  // connect nodes with function
  nodes.foreach(_._func = this)

  // backward edges
  val backwards: Map[Node, Set[(Edge, Node)]] = (for {
    (from, set) <- forwards
    (edge, to) <- set
  } yield (to, edge, from)).groupBy(_._1).map {
    case (k, y) => k -> y.map(x => (x._2, x._3)).toSet
  }.toMap

  // conversion to DOT
  def toDot: String = (new DotPrinter)(this).toString
}
