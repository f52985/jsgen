package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.{ AllocSite => _, _ }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.spec.algorithm._
import scala.annotation.tailrec

// abstract transfer function
case class AbsTransfer(sem: AbsSemantics) {
  import AbsState.monad._

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint[_]) => this(np)
    case (rp: ReturnPoint) => this(rp)
  }

  // transfer function for node points
  def apply[T <: Node](np: NodePoint[T]): Unit = {
    val st = sem(np)
    val NodePoint(node, view) = np
    val helper = new Helper(np)

    import helper._
    node match {
      case (entry: Entry) =>
        sem += NodePoint(cfg.nextOf(entry), view) -> st
      case (exit: Exit) => ???
      case (normal: Normal) =>
        val newSt = transfer(normal.inst)(st)
        sem += NodePoint(cfg.nextOf(normal), view) -> newSt
      case (call: Call) => transfer(call, view)(st)
      case arrow @ Arrow(_, inst, fid) => ???
      case branch @ Branch(_, inst) => ???
    }
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = ???

  // internal transfer function with a specific view
  private class Helper(val cp: ControlPoint) {
    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = inst match {
      case IExpr(expr) => ???
      case ILet(id, expr) => for {
        v <- transfer(expr)
        _ <- modify(_.defineLocal(id -> v))
      } yield ()
      case IAssign(ref, expr) => for {
        rv <- transfer(ref)
        v <- transfer(expr)
        _ <- modify(_.update(rv, v))
      } yield ()
      case IDelete(ref) => ???
      case IAppend(expr, list) => ???
      case IPrepend(expr, list) => ???
      case IReturn(expr) => ???
      case IThrow(name) => ???
      case IAssert(expr) => ???
      case IPrint(expr) => ???
    }

    // transfer function for calls
    def transfer(call: Call, view: View): Updater = call.inst match {
      // TODO `simpleFuncs contains name` case
      case IApp(id, fexpr, args) => for {
        value <- transfer(fexpr)
        vs <- join(args.map(transfer))
        st <- get
      } yield {
        // algorithms
        for (algo <- value.func) {
          val head = algo.head
          val body = algo.body
          val locals = getLocals(head.params, vs)
          val newSt = st.copy(locals = locals)
          sem.doCall(call, view, algo.func, newSt)
        }

        // closures
        for (clo <- value.clo) ???

        // continuations
        for (cont <- value.cont) ???

        AbsState.Bot
      }
      case IAccess(id, bexpr, expr, args) => ???
    }

    // transfer function for expressions
    def transfer(expr: Expr): Result[AbsValue] = expr match {
      case ENum(n) => AbsValue(Num(n))
      case EINum(l) => AbsValue(l)
      case EBigINum(b) => AbsValue(b)
      case EStr(str) => AbsValue(str)
      case EBool(b) => AbsValue(b)
      case EUndef => AbsValue.undef
      case ENull => AbsValue.nullv
      case EAbsent => AbsValue.absent
      case EConst(name) => ???
      case EMap(Ty("Completion"), props) => ???
      case map @ EMap(ty, props) => {
        val loc: Loc = AllocSite(map.asite, cp.view)
        for {
          _ <- modify(_.allocMap(ty)(loc))
          _ <- join(props.map {
            case (kexpr, vexpr) => for {
              k <- transfer(kexpr)
              v <- transfer(vexpr)
              _ <- modify(_.update(loc, k, v))
            } yield ()
          })
        } yield AbsValue(loc)
      }
      case EList(exprs) => ???
      case ESymbol(desc) => ???
      case EPop(list, idx) => ???
      case ERef(ref) => for {
        rv <- transfer(ref)
        v <- transfer(rv)
      } yield v
      case EUOp(uop, expr) => ???
      case EBOp(OAnd, left, right) => ???
      case EBOp(OOr, left, right) => ???
      case EBOp(OEq, ERef(RefId(id)), EAbsent) => ???
      case EBOp(bop, left, right) => ???
      case ETypeOf(expr) => ???
      case EIsCompletion(expr) => ???
      case EIsInstanceOf(base, name) => ???
      case EGetElems(base, name) => ???
      case EGetSyntax(base) => ???
      case EParseSyntax(code, rule, parserParams) => ???
      case EConvert(source, target, flags) => ???
      case EContains(list, elem) => ???
      case EReturnIfAbrupt(rexpr @ ERef(ref), check) => ???
      case EReturnIfAbrupt(expr, check) => ???
      case ECopy(obj) => ???
      case EKeys(mobj, intSorted) => ???
      case ENotSupported(msg) => ???
    }

    // transfer function for references
    def transfer(ref: Ref): Result[AbsRefValue] = ref match {
      case RefId(id) => AbsRefId(id)
      case RefProp(ref, expr) => for {
        rv <- transfer(ref)
        b <- transfer(rv)
        p <- transfer(expr)
      } yield AbsRefProp(b, p)
    }

    // transfer function for reference values
    def transfer(rv: AbsRefValue): Result[AbsValue] = for {
      v <- get(_(rv, cp))
    } yield v

    // get initial local variables
    def getLocals(
      params: List[Param],
      args: List[AbsValue]
    ): Map[Id, AbsValue] = {
      var map = Map[Id, AbsValue]()

      @tailrec
      def aux(ps: List[Param], as: List[AbsValue]): Unit = (ps, as) match {
        case (Nil, Nil) =>
        case (Param(name, kind) :: pl, Nil) => {
          map += Id(name) -> AbsValue.absent
          aux(pl, Nil)
        }
        case (param :: pl, arg :: al) => {
          map += Id(param.name) -> arg
          aux(pl, al)
        }
        case _ =>
      }

      aux(params, args)
      map
    }
  }
}
