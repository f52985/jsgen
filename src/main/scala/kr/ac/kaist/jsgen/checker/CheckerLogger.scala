package kr.ac.kaist.jsgen.checker

import kr.ac.kaist.jsgen.{ LINE_SEP, LOG, CHECK_LOG_DIR }
import kr.ac.kaist.jsgen.util.Appender
import kr.ac.kaist.jsgen.util.JvmUseful._
import kr.ac.kaist.jsgen.util.Useful._
import scala.Console._
import java.io.PrintWriter

object CheckerLogger {
  // iteration
  var iter = 0
  private var counter: Map[ControlPoint, Int] = Map()

  // initalize
  mkdir(CHECK_LOG_DIR)
  private val nf = getPrintWriter(s"$CHECK_LOG_DIR/summary.tsv")

  // time
  var parseTime = 0L
  var checkerTime = 0L
  var checkStartTime = 0L
  def checkTime: Long = System.currentTimeMillis - checkStartTime

  // checker time
  def doCheck[T](f: => T): T = {
    val (t, res) = time(f)
    checkerTime += t
    res
  }

  // increase counter
  def inc[T <: ControlPoint](cp: T): T = {
    if (LOG) counter += cp -> (counter.getOrElse(cp, 0) + 1)
    cp
  }

  // Abbreviation
  log("#", "The number of iterations")
  log("T", "The duration time (ms)")
  log("WL", "The number of control points in worklist")
  log("CP", "The number of checked control points")
  log("AU", "The avarage number of updates for control points")
  log("RP", "The number of checked return points")
  log("AF", "The number of checked functions")
  log("TF", "The number of total functions")
  log("ER", "The number of detected errors")
  log("WA", "The number of detected warnings")
  log()

  // header
  log("#", "T", "WL", "CP", "AU", "RP", "AF", "TF", "ER", "WA")

  // dump logs
  def dump(): Unit = {
    val (numFunc, numAlgo, numRp) = sem.numOfFuncAlgoRp
    val ctime = checkTime

    // dump summary
    log(
      f"$iter%,3d", f"$ctime%,3d", worklist.size, sem.size,
      f"$avg%.2f", numRp, numFunc, numAlgo, numBug, numWarn
    )

    // dump worklist
    val wapp = new Appender
    worklist.foreach(wapp >> _.toString >> LINE_SEP)
    dumpFile(wapp, s"$CHECK_LOG_DIR/worklist")

    // the number of full functions
    val fullFuncNum = sem.rpMap.keySet.count(_.func.complete)

    // dump logs for evaluation
    // # iter, parse, checker, check, full, all, node, return, all
    val evalItems = List(
      iter,
      parseTime,
      checkerTime,
      ctime,
      fullFuncNum,
      numFunc,
      sem.npMap.size,
      numRp,
      sem.npMap.size + numRp
    )
    dumpFile(evalItems.map(_.toString).mkString("\t"), s"$CHECK_LOG_DIR/summary")
  }

  // close
  def close(): Unit = nf.close()

  // log helpers
  private def log(items: Any*): Unit = {
    nf.println(items.map(_.toString).mkString("\t"))
    nf.flush()
  }
  private def min: Int = if (counter.isEmpty) -1 else counter.values.min
  private def max: Int = if (counter.isEmpty) -1 else counter.values.max
  private def avg: Double =
    if (counter.isEmpty) -1
    else counter.values.sum / counter.size.toDouble
  private def median: Double =
    if (counter.isEmpty) -1
    else {
      val size = counter.size
      val values = counter.values.toList.sorted
      if (size % 2 == 1) values(size / 2)
      else (values(size / 2) + values(size / 2 - 1)) / 2.toDouble
    }
}
