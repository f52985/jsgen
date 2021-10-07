package kr.ac.kaist.jsgen.phase

import kr.ac.kaist.jsgen.JSGenConfig
import kr.ac.kaist.jsgen.util.ArgParser

sealed abstract class PhaseList[Result] {
  def getRunner(
    parser: ArgParser
  ): JSGenConfig => Result

  def >>[C <: Config, R](phase: Phase[Result, C, R]): PhaseList[R] = PhaseCons(this, phase)

  val nameList: List[String]
  override def toString: String = nameList.reverse.mkString(" >> ")
}

case object PhaseNil extends PhaseList[Unit] {
  def getRunner(
    parser: ArgParser
  ): JSGenConfig => Unit = x => {}

  val nameList: List[String] = Nil
}

case class PhaseCons[P, C <: Config, R](
  prev: PhaseList[P],
  phase: Phase[P, C, R]
) extends PhaseList[R] {
  def getRunner(
    parser: ArgParser
  ): JSGenConfig => R = {
    val prevRunner = prev.getRunner(parser)
    val phaseRunner = phase.getRunner(parser)
    jsgenConfig => phaseRunner(prevRunner(jsgenConfig), jsgenConfig)
  }

  val nameList: List[String] = phase.name :: prev.nameList
}

