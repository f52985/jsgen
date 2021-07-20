package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.JvmUseful._

// partial models
object PartialModel {
  // views for each function
  lazy val viewMap: Map[Function, List[View]] = {
    sem.rpMap.keySet.toList.groupBy(_.func).map {
      case (func, rps) => func -> rps.map(_.view)
    }
  }

  // partial models from result of type check
  lazy val models: Map[Algo, Map[View, Inst]] =
    (for (func <- cfg.funcs) yield func.algo -> getModel(func)).toMap

  // get partial models for an algorithm
  def getModel(func: Function): Map[View, Inst] = {
    val body = func.algo.getBody
    (for {
      view <- viewMap.getOrElse(func, Nil)
      model = getModel(func, body, view)
    } yield view -> model).toMap
  }

  // get partial models for an algorithm with a specific view
  def getModel(func: Function, body: Inst, view: View): Inst = {
    body // TODO implement
  }

  // get string of a partial model
  def getString(app: Appender, view: View, inst: Inst): Appender = {
    app >> view.toString >> ":"
    app >> inst.beautified(index = true) >> LINE_SEP
  }
  def getString(view: View, inst: Inst): String =
    getString(new Appender, view, inst).toString

  // get string fo partial models for a specific algorithm
  def getString(app: Appender, algo: Algo): Appender = {
    models.getOrElse(algo, Map()).foldLeft(app) {
      case (app, (view, inst)) => getString(app, view, inst)
    }
  }
  def getString(algo: Algo): String = getString(new Appender, algo).toString
}
