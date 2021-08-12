package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir.{ AllocSite => _, _ }
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract heaps
object BasicHeap extends Domain {
  lazy val Bot = Elem(Map(), Set(), Set(), Set())

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    val Elem(map, allocs, touched, merged) = elem
    if (elem.isBottom) app >> "{}"
    else app.wrap {
      map.toList
        .sortBy(_._1.toString)
        .foreach {
          case (k, v) =>
            app :> "["
            app >> (if (allocs contains k) "A" else " ")
            app >> (if (touched contains k) "T" else " ")
            app >> (if (merged contains k) "M" else " ")
            app >> "] " >> s"$k -> " >> v >> LINE_SEP
        }
    }
  }

  // constructors
  def apply(
    map: Map[Loc, AbsObj] = Map(),
    allocs: Set[Loc] = Set(),
    touched: Set[Loc] = Set(),
    merged: Set[Loc] = Set()
  ): Elem = Elem(map, allocs, touched, merged)

  // extractors
  def unapply(elem: Elem) = Some((
    elem.map,
    elem.merged,
  ))

  // elements
  case class Elem(
    map: Map[Loc, AbsObj],
    allocs: Set[Loc],
    touched: Set[Loc],
    merged: Set[Loc]
  ) extends ElemTrait {
    // partial order
    override def isBottom = map.isEmpty

    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case _ if this.isBottom => true
      case _ if that.isBottom => false
      case (l, r) => (
        (l.map.keySet ++ r.map.keySet).forall(loc => {
          this(loc) ⊑ that(loc)
        }) &&
        (l.allocs subsetOf r.allocs) &&
        (l.touched subsetOf r.touched) &&
        (l.merged subsetOf r.merged)
      )
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case _ if this.isBottom => that
      case _ if that.isBottom => this
      case (l, r) => Elem(
        map = (l.map.keySet ++ r.map.keySet).toList.map(loc => {
          loc -> this(loc) ⊔ that(loc)
        }).toMap,
        allocs = l.allocs ++ r.allocs,
        touched = l.touched ++ r.touched,
        merged = l.merged ++ r.merged,
      )
    }

    // singleton checks
    def isSingle: Boolean = map.forall {
      case (loc, obj) => isSingle(loc) && obj.isSingle
    }

    // singleton location checks
    def isSingle(aloc: AbsLoc): Boolean = aloc.getSingle match {
      case FlatElem(loc) => isSingle(loc)
      case _ => false
    }
    def isSingle(loc: Loc): Boolean = !(merged contains loc)

    // handle calls
    def doCall: Elem = copy(allocs = Set(), touched = Set())

    // TODO handle returns (this: caller heaps / retHeap: return heaps)
    def <<(retHeap: Elem): Elem = retHeap
    // Elem(
    //   map = retHeap.touched.foldLeft(this.map) {
    //     case (map, loc) => map + (loc -> retHeap(loc))
    //   },
    //   allocs = this.allocs ++ retHeap.allocs,
    //   touched = this.touched ++ retHeap.touched,
    //   merged = (
    //     this.merged ++
    //     retHeap.merged ++
    //     (this.map.keySet intersect retHeap.allocs)
    //   ),
    // )

    // get reachable locations
    def reachableLocs(initLocs: Set[Loc]): Set[Loc] = {
      var locs = Set[Loc]()
      def aux(loc: Loc): Unit = if (!locs.contains(loc)) {
        locs += loc
        val objLocs = this(loc).reachableLocs
        objLocs.foreach(aux)
      }
      initLocs.foreach(aux)
      locs
    }

    // remove given locations
    def removeLocs(locs: Loc*): Elem = removeLocs(locs.toSet)
    def removeLocs(locs: Set[Loc]): Elem = Elem(
      map -- locs,
      allocs -- locs,
      touched -- locs,
      merged -- locs,
    )

    // lookup abstract locations
    def apply(loc: Loc): AbsObj =
      map.getOrElse(loc, base.getOrElse(loc, AbsObj.Bot))
    def apply(loc: AbsLoc, prop: AbsValue): AbsValue =
      loc.map(this(_, prop)).foldLeft(AbsValue.Bot: AbsValue)(_ ⊔ _)
    def apply(loc: Loc, prop: AbsValue): AbsValue = loc match {
      case NamedLoc(js.ALGORITHM) =>
        prop.str
          .map(str => AbsValue(initHeap.getAlgorithm(str)))
          .foldLeft(AbsValue.Bot: AbsValue)(_ ⊔ _)
      case NamedLoc(js.INTRINSICS) =>
        prop.str
          .map(str => AbsValue(initHeap.getIntrinsics(str)))
          .foldLeft(AbsValue.Bot: AbsValue)(_ ⊔ _)
      case _ => this(loc)(prop)
    }

    // setters
    def update(loc: AbsLoc, prop: AbsValue, value: AbsValue): Elem =
      applyEach(loc)(_.update(prop, value, _))

    // delete
    def delete(loc: AbsLoc, prop: AbsValue): Elem =
      applyEach(loc)(_.delete(prop, _))

    // appends
    def append(loc: AbsLoc, value: AbsValue): Elem =
      applyEach(loc)(_.append(value, _))

    // prepends
    def prepend(loc: AbsLoc, value: AbsValue): Elem =
      applyEach(loc)(_.prepend(value, _))

    // pops
    def pop(loc: AbsLoc, idx: AbsValue): (AbsValue, Elem) = {
      var v: AbsValue = AbsValue.Bot
      val h: Elem = applyEach(loc)((obj, weak) => {
        val (newV, newObj) = obj.pop(idx, weak)
        v ⊔= newV
        newObj
      })
      (v, h)
    }

    // copy objects
    def copyObj(
      from: AbsLoc
    )(to: AllocSite): Elem = alloc(to, applyFold(from)(obj => obj))

    // keys of map
    def keys(
      loc: AbsLoc,
      intSorted: Boolean
    )(to: AllocSite): Elem = alloc(to, applyFold(loc)(_.keys(intSorted)))

    // map allocations
    def allocMap(
      ty: Ty,
      pairs: List[(AbsValue, AbsValue)]
    )(to: AllocSite): Elem = {
      val newObj = (pairs.foldLeft(AbsObj(IRMap(ty))) {
        case (m, (k, v)) => m.update(k, v, weak = false)
      })
      if (ty.hasSubMap) {
        val subMapLoc = SubMapLoc(to)
        val subMapObj = AbsObj.MapElem(Ty("SubMap"), Map(), Vector())
        this
          .alloc(to, newObj.update(AbsValue("SubMap"), AbsValue(subMapLoc), weak = false))
          .alloc(subMapLoc, subMapObj)
      } else this.alloc(to, newObj)
    }

    // list allocations
    def allocList(
      values: Iterable[AbsValue] = Nil
    )(to: AllocSite): Elem = alloc(to, AbsObj.ListElem(values.toVector))

    // symbol allocations
    def allocSymbol(
      desc: AbsValue
    )(to: AllocSite): Elem = alloc(to, AbsObj.SymbolElem(desc))

    // allocation helper
    private def alloc(loc: Loc, obj: AbsObj): Elem = this(loc) match {
      case AbsObj.Bot => Elem(
        map = map + (loc -> obj),
        allocs = allocs + loc,
        touched = touched + loc,
        merged = merged,
      )
      case _ => Elem(
        map = map + (loc -> (this(loc) ⊔ obj)),
        allocs = allocs + loc,
        touched = touched + loc,
        merged = merged + loc
      )
    }

    // set type of objects
    def setType(loc: AbsLoc, ty: Ty): Elem =
      applyEach(loc)((obj, _) => obj.setType(ty))

    // check contains
    def contains(loc: AbsLoc, value: AbsValue): AbsBool = {
      loc.toList.foldLeft(AbsBool.Bot: AbsBool) {
        case (bool, loc) => bool ⊔ (this(loc) contains value)
      }
    }

    // helper for abstract locations
    private def applyEach(loc: AbsLoc)(
      f: (AbsObj, Boolean) => AbsObj
    ): Elem = {
      val weak = !isSingle(loc)
      loc.toList.foldLeft(this) {
        case (heap, loc) =>
          val obj = heap(loc)
          val newObj = f(obj, weak)
          heap.copy(
            map = heap.map + (loc -> newObj),
            touched = touched + loc,
          )
      }
    }
    private def applyFold(loc: AbsLoc)(f: AbsObj => AbsObj): AbsObj = {
      loc.toList.foldLeft(AbsObj.Bot: AbsObj) {
        case (obj, loc) => obj ⊔ f(this(loc))
      }
    }
  }

  // initial conrete heap
  lazy val initHeap: Heap = js.Initialize.initHeap

  // base mapping from locations to abstract objects
  lazy val base: Map[Loc, AbsObj] = (for {
    (addr, obj) <- initHeap.map
    loc = Loc.from(addr)
    aobj = AbsObj(obj)
  } yield loc -> aobj).toMap
}
