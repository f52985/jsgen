package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract objects
object BasicObj extends Domain {
  case object Bot extends Elem
  case object Top extends Elem
  case class SymbolElem(desc: AbsValue) extends Elem
  case class MergedMapElem(ty: Ty, value: AbsValue) extends Elem
  case class MapElem(ty: Ty, map: Map[AValue, AbsValue]) extends Elem
  case class MergedListElem(value: AbsValue) extends Elem
  case class ListElem(values: Vector[AbsValue]) extends Elem
  case class NotSupportedElem(desc: String) extends Elem

  // abstraction functions
  def apply(obj: Obj): Elem = obj match {
    case IRSymbol(desc) => SymbolElem(AbsValue(desc))
    case IRMap(ty, props, size) => MapElem(ty, (props.toList.map {
      case (k, (v, _)) => AValue.from(k) -> AbsValue(v)
    }).toMap)
    case IRList(values) => ListElem(values.map(AbsValue(_)))
    case IRNotSupported(tyname, desc) => NotSupportedElem(desc)
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => elem match {
    case Bot => app >> "⊥"
    case Top => app >> "⊤"
    case SymbolElem(desc) => app >> "'" >> desc.toString
    case MergedMapElem(ty, value) =>
      app >> ty.toString >> "{{" >> value.toString >> "}}"
    case MapElem(ty, map) =>
      app >> ty.toString
      app.wrap {
        for ((k, v) <- map) app >> s"$k -> $v" >> LINE_SEP
      }
    case MergedListElem(value) =>
      app >> "[[" >> value.toString >> "]]"
    case ListElem(values) =>
      app >> values.mkString("[", ", ", "]")
    case NotSupportedElem(desc) =>
      app >> "???(" >> desc >> ")"
  }

  // elements
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case BasicOrder(bool) => bool
      case (SymbolElem(ldesc), SymbolElem(rdesc)) => ldesc ⊑ rdesc
      case (MergedMapElem(lty, lv), MergedMapElem(rty, rv)) =>
        lty == rty && lv ⊑ rv
      case (MapElem(lty, lmap), MapElem(rty, rmap)) =>
        lty == rty && (lmap.keySet ++ rmap.keySet).forall(x => {
          this(x) ⊑ that(x)
        })
      case (lmap @ MapElem(lty, _), MergedMapElem(rty, rv)) =>
        lty == rty && lmap.mergedValue ⊑ rv
      case (MergedListElem(lv), MergedListElem(rv)) =>
        lv ⊑ rv
      case (ListElem(lvs), ListElem(rvs)) =>
        lvs.length == rvs.length && (lvs zip rvs).forall { case (l, r) => l ⊑ r }
      case (ListElem(_), MergedListElem(rv)) =>
        this.mergedValue ⊑ rv
      case (NotSupportedElem(ld), NotSupportedElem(rd)) => ld == rd
      case _ => false
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case BasicJoin(elem) => elem
      case _ if this ⊑ that => that
      case _ if that ⊑ this => this
      case (SymbolElem(ldesc), SymbolElem(rdesc)) => SymbolElem(ldesc ⊔ rdesc)
      case (MergedMapElem(lty, lv), MergedMapElem(rty, rv)) if lty == rty =>
        MergedMapElem(lty, lv ⊔ rv)
      case (MapElem(lty, lmap), MapElem(rty, rmap)) if lty == rty =>
        MapElem(lty, (lmap.keySet ++ rmap.keySet).toList.map(x => {
          x -> this(x) ⊔ that(x)
        }).toMap)
      case (MapElem(lty, _), MergedMapElem(rty, rv)) if lty == rty =>
        MergedMapElem(lty, this.mergedValue ⊔ rv)
      case (MergedListElem(lv), MergedListElem(rv)) =>
        MergedListElem(lv ⊔ rv)
      case (ListElem(lvs), ListElem(rvs)) => if (lvs.length == rvs.length) {
        ListElem((lvs zip rvs).map { case (l, r) => l ⊔ r })
      } else MergedListElem(this.mergedValue ⊔ that.mergedValue)
      case (ListElem(lvs), MergedListElem(rv)) =>
        MergedListElem(this.mergedValue ⊔ rv)
      case (NotSupportedElem(ld), NotSupportedElem(rd)) if ld == rd => this
      case _ => AbsObj.Top
    }

    // lookup
    def apply(key: AValue): AbsValue = this match {
      case Bot => AbsValue.Bot
      case Top => AbsValue.Top
      case SymbolElem(desc) => key match {
        case ASimple(Str("Description")) => desc
        case _ => AbsValue.Bot
      }
      case MergedMapElem(_, value) => value
      case MapElem(_, map) => map.getOrElse(key, AbsValue.absent)
      case MergedListElem(value) => value
      case ListElem(values) => key match {
        case ASimple(INum(long)) =>
          val idx = long.toInt
          if (0 <= idx && idx < values.length) values(idx)
          else AbsValue.absent
        case ASimple(Str("length")) => AbsValue(values.length)
        case _ => AbsValue.Bot
      }
      case NotSupportedElem(desc) => AbsValue.Bot
    }

    // abstract lookup
    def apply(akey: AbsValue): AbsValue = akey.getSingle match {
      case FlatBot => AbsValue.Bot
      case FlatTop => AbsValue.Top
      case FlatElem(key) => this(key)
      case _ => mergedValue
    }

    // merged value of all possible values
    lazy val mergedValue: AbsValue = this match {
      case Bot => AbsValue.Bot
      case Top => AbsValue.Top
      case SymbolElem(desc) => desc
      case MergedMapElem(_, value) => value
      case MapElem(_, map) => map.foldLeft[AbsValue](AbsValue.Bot) {
        case (lv, (_, rv)) => lv ⊔ rv
      }
      case MergedListElem(value) => value
      case ListElem(values) => values.foldLeft[AbsValue](AbsValue.Bot)(_ ⊔ _)
      case NotSupportedElem(desc) => AbsValue.Bot
    }

    // updates
    def update(
      prop: AbsValue,
      value: AbsValue,
      weak: Boolean = false
    ): Elem = this match {
      case MergedMapElem(ty, mergedValue) =>
        MergedMapElem(ty, mergedValue ⊔ value)
      case MapElem(Ty("SubMap"), map) =>
        ???
      case MapElem(ty, map) => prop.simple.str.getSingle match {
        case FlatBot => this
        case FlatElem(str) =>
          val key = ASimple(Str(str))
          val newValue = if (weak) this(key) ⊔ value else value
          MapElem(ty, map + (key -> newValue))
        case FlatTop => MergedMapElem(ty, mergedValue)
      }
      case _ => this
    }
  }
}
