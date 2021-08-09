package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.{ AllocSite => _, _ }
import kr.ac.kaist.jiset.js.{ Parser => ESParser, _ }
import kr.ac.kaist.jiset.js.ast.Lexical
import kr.ac.kaist.jiset.util.Useful._
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
        sem += getNextNp(np, cfg.nextOf(entry)) -> st
      case (exit: Exit) =>
        ???
      case (normal: Normal) =>
        val newSt = transfer(normal.inst)(st)
        sem += getNextNp(np, cfg.nextOf(normal)) -> newSt
      case (call: Call) =>
        val newSt = transfer(call, view)(st)
        sem += getNextNp(np, cfg.nextOf(call)) -> newSt
      case arrow @ Arrow(_, inst, fid) =>
        ???
      case branch @ Branch(_, inst) => (for {
        v <- transfer(inst.cond)
        b = v.escaped.bool
        st <- get
      } yield {
        val (thenNode, elseNode) = cfg.branchOf(branch)
        if (b contains T) sem += getNextNp(np, thenNode) -> st
        if (b contains F) sem += getNextNp(np, elseNode, true) -> st
      })(st)
      case (cont: LoopCont) =>
        sem += getNextNp(np, cfg.nextOf(cont)) -> st
    }
  }

  // get next node points
  def getNextNp(
    fromCp: NodePoint[Node],
    to: Node,
    loopOut: Boolean = false
  ): NodePoint[Node] = {
    val NodePoint(from, view) = fromCp
    val toView = (from, to) match {
      case (_: LoopCont, _: Loop) => view.loopNext
      case (_, loop: Loop) => view.loopEnter(loop)
      case (_: Loop, _) if loopOut => view.loopExit
      case _ => view
    }
    NodePoint(to, toView)
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    var ret @ AbsRet(value, st) = sem(rp)

    // proper type handle
    Interp.setTypeMap.get(rp.func.name).map(ty => {
      value = AbsValue(loc = value.loc)
      st = st.setType(value.loc, ty)
    })

    // debugging message
    if (DEBUG) println(s"<RETURN> $ret")

    // return wrapped values
    for (np @ NodePoint(call, view) <- sem.getRetEdges(rp)) {
      val nextNode = cfg.nextOf(call)
      val nextNP = NodePoint(nextNode, nextNode match {
        case loop: Loop => view.loopEnter(loop)
        case _ => view
      })
      val callerLocals = sem.callInfo.getOrElse(np, Map())
      val newSt = st
        .copy(locals = callerLocals)
        .defineLocal(call.inst.id -> value.wrapCompletion)
      // TODO more precise heap merge by keeping touched locations
      sem += nextNP -> newSt
    }
  }

  // internal transfer function with a specific view
  private class Helper(val cp: ControlPoint) {
    lazy val func = sem.funcOf(cp)
    lazy val rp = ReturnPoint(func, cp.view)

    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = inst match {
      case IExpr(expr) => for {
        v <- transfer(expr)
      } yield v
      case ILet(id, expr) => for {
        v <- transfer(expr)
        _ <- modify(_.defineLocal(id -> v))
      } yield ()
      case IAssign(ref, expr) => for {
        rv <- transfer(ref)
        v <- transfer(expr)
        _ <- modify(_.update(rv, v))
      } yield ()
      case IDelete(ref) => for {
        rv <- transfer(ref)
        _ <- modify(_.delete(rv))
      } yield ()
      case IAppend(expr, list) => for {
        l <- transfer(list)
        loc = l.escaped.loc
        v <- transfer(expr)
        _ <- modify(_.append(l.escaped.loc, v.escaped))
      } yield ()
      case IPrepend(expr, list) => for {
        l <- transfer(list)
        loc = l.escaped.loc
        v <- transfer(expr)
        _ <- modify(_.prepend(l.escaped.loc, v.escaped))
      } yield ()
      case IReturn(expr) => for {
        v <- transfer(expr)
        _ <- doReturn(v)
        _ <- put(AbsState.Bot)
      } yield ()
      case thr @ IThrow(name) => {
        val loc: AllocSite = AllocSite(thr.asite, cp.view)
        for {
          _ <- modify(_.allocMap(Ty("OrdinaryObject"), List(
            AbsValue(Str("Prototype")) -> AbsValue(NamedLoc(s"GLOBAL.$name.prototype")),
            AbsValue(Str("ErrorData")) -> AbsValue(Undef),
          ))(loc))
          _ <- doReturn(AbsValue(loc).wrapCompletion("throw"))
          _ <- put(AbsState.Bot)
        } yield ()
      }
      case IAssert(expr) => for {
        v <- transfer(expr)
      } yield ()
      case IPrint(expr) => st => st
    }

    // return specific value
    def doReturn(v: AbsValue): Result[Unit] = for {
      st <- get
      ret = AbsRet(v, st)
      _ = sem.doReturn(rp, ret)
    } yield ()

    // transfer function for calls
    def transfer(call: Call, view: View): Updater = call.inst match {
      case IApp(id, ERef(RefId(Id(name))), args) if simpleFuncs contains name => {
        for {
          as <- join(args.map(transfer))
          vs = if (name == "IsAbruptCompletion") as else as.map(_.escaped)
          st <- get
          v <- simpleFuncs(name)(vs)
          _ <- modify(_.defineLocal(id -> v))
        } yield ()
      }
      case IApp(id, fexpr, args) => for {
        value <- transfer(fexpr)
        vs <- join(args.map(transfer))
        st <- get
        _ <- put(AbsState.Bot)
      } yield {
        // algorithms
        for (AFunc(algo) <- value.func) {
          val head = algo.head
          val body = algo.body
          val locals = getLocals(head.params, vs)
          val newSt = st.copy(locals = locals)
          sem.doCall(call, view, algo.func, st.locals, newSt)
        }

        // closures
        for (clo <- value.clo) ???

        // continuations
        for (cont <- value.cont) ???
      }
      case access @ IAccess(id, bexpr, expr, args) => {
        val loc: AllocSite = AllocSite(access.asite, cp.view)
        for {
          base <- transfer(bexpr)
          b = base.escaped
          prop <- transfer(expr)
          p = prop.escaped
          astV <- (b.ast.getSingle, p.str.getSingle) match {
            case (FlatElem(AAst(ast)), FlatElem(Str(name))) => (ast, name) match {
              case (Lexical(kind, str), name) =>
                pure(AbsValue(Interp.getLexicalValue(kind, name, str)))
              case (ast, "parent") =>
                pure(ast.parent.map(AbsValue(_)).getOrElse(AbsValue.absent))
              case (ast, "children") => for {
                _ <- modify(_.allocList(ast.children.map(AbsValue(_)))(loc))
              } yield AbsValue(loc)
              case (ast, "kind") =>
                pure(AbsValue(ast.kind))
              case _ => ast.semantics(name) match {
                case Some((algo, asts)) => for {
                  as <- join(args.map(transfer))
                  head = algo.head
                  body = algo.body
                  vs = asts.map(AbsValue(_)) ++ as
                  locals = getLocals(head.params, vs)
                  callerLocals <- get(_.locals)
                  newSt <- get(_.copy(locals = locals))
                  _ = sem.doCall(call, view, algo.func, callerLocals, newSt)
                } yield AbsValue.Bot
                case None =>
                  val v = AbsValue(ast.subs(name).getOrElse {
                    error(s"unexpected semantics: ${ast.name}.$name")
                  })
                  pure(v)
              }
            }
            case _ => error("impossible to handle generic access of ASTs")
          }
          otherV <- get(_(base, p))
          value = astV ⊔ otherV
          _ <- {
            if (!value.isBottom) modify(_.defineLocal(id -> value))
            else put(AbsState.Bot)
          }
        } yield ()
      }
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
      case EConst(name) => AbsValue(AConst(name))
      case EMap(Ty("Completion"), props) => ???
      case map @ EMap(ty, props) => {
        val loc: AllocSite = AllocSite(map.asite, cp.view)
        for {
          pairs <- join(props.map {
            case (kexpr, vexpr) => for {
              k <- transfer(kexpr)
              v <- transfer(vexpr)
            } yield (k, v)
          })
          _ <- modify(_.allocMap(ty, pairs)(loc))
        } yield AbsValue(loc)
      }
      case list @ EList(exprs) => {
        val loc: AllocSite = AllocSite(list.asite, cp.view)
        for {
          vs <- join(exprs.map(transfer))
          _ <- modify(_.allocList(vs.map(_.escaped))(loc))
        } yield AbsValue(loc)
      }
      case symbol @ ESymbol(desc) => {
        val loc: AllocSite = AllocSite(symbol.asite, cp.view)
        for {
          v <- transfer(desc)
          newV = AbsValue(str = v.str, undef = v.undef)
          _ <- modify(_.allocSymbol(newV)(loc))
        } yield AbsValue(loc)
      }
      case EPop(list, idx) => for {
        l <- transfer(list)
        loc = l.escaped.loc
        k <- transfer(idx)
        v <- id(_.pop(loc, k.escaped))
      } yield v
      case ERef(ref) => for {
        rv <- transfer(ref)
        v <- transfer(rv)
      } yield v
      case EUOp(uop, expr) => for {
        x <- transfer(expr)
        v <- get(transfer(_, uop, x.escaped))
      } yield v
      case EBOp(OAnd, left, right) => shortCircuit(OAnd, left, right)
      case EBOp(OOr, left, right) => shortCircuit(OOr, left, right)
      case EBOp(OEq, ERef(ref), EAbsent) => for {
        rv <- transfer(ref)
        b <- get(_.exists(rv))
      } yield AbsValue(bool = !b)
      case EBOp(bop, left, right) => for {
        l <- transfer(left)
        r <- transfer(right)
        v <- get(transfer(_, bop, l.escaped, r.escaped))
      } yield v
      case ETypeOf(expr) => for {
        v <- transfer(expr)
        value = v.escaped
        st <- get
      } yield value.getSingle match {
        case FlatBot => AbsValue.Bot
        case FlatElem(v) => AbsValue(v match {
          case _: AComp => "Completion"
          case _: AConst => "Constant"
          case loc: Loc => st(loc).getTy.name match {
            case name if name endsWith "Object" => "Object"
            case name => name
          }
          case _: AFunc => "Function"
          case _: AClo => "Closure"
          case _: ACont => "Continuation"
          case _: AAst => "AST"
          case ASimple(_: Num | _: INum) => "Number"
          case ASimple(_: BigINum) => "BigInt"
          case ASimple(_: Str) => "String"
          case ASimple(_: Bool) => "Boolean"
          case ASimple(Undef) => "Undefined"
          case ASimple(Null) => "Null"
          case ASimple(Absent) => "Absent"
        })
        case FlatTop => AbsValue.str
      }
      case EIsCompletion(expr) => for {
        v <- transfer(expr)
      } yield AbsValue(bool = v.isCompletion)
      case EIsInstanceOf(base, name) => for {
        bv <- transfer(base)
        st <- get
      } yield AbsValue(bool = AbsBool((for {
        Bool(b) <- bv.isAbruptCompletion.toSet
        resB <- if (b) Set(false) else {
          var set = Set[Boolean]()
          val escapedV = bv.escaped
          for (AAst(ast) <- escapedV.ast.toList) {
            set += ast.name == name || ast.getKinds.contains(name)
          }
          for (Str(str) <- escapedV.str.toList) set += str == name
          for (loc <- escapedV.loc.toList) set += st(loc).getTy < Ty(name)
          val otherV = escapedV.copy(
            ast = AbsAST.Bot,
            loc = AbsLoc.Bot,
            simple = escapedV.simple.copy(str = AbsStr.Bot),
          )
          if (!otherV.isBottom) set += false
          set
        }
      } yield resB).map(Bool(_))))
      case EGetElems(base, name) => ???
      case EGetSyntax(base) => for {
        v <- transfer(base)
        s = AbsStr(v.escaped.ast.toList.map(x => Str(x.ast.toString)))
      } yield AbsValue(str = s)
      case EParseSyntax(code, rule, parserParams) => for {
        value <- transfer(code)
        v = value.escaped
        ruleV <- transfer(rule)
        p = ruleV.escaped.str.getSingle match {
          case FlatElem(Str(str)) =>
            ESParser.rules.getOrElse(str, error(s"not exist parse rule: $rule"))
          case _ => ???
        }
        st <- get
      } yield AbsValue(v.getSingle match {
        case FlatElem(AAst(ast)) =>
          ESParser.parse(p(ast.parserParams), ast.toString).get.checkSupported
        case FlatElem(ASimple(Str(str))) => {
          ESParser.parse(p(parserParams), str).get.checkSupported
        }
        case v => error(s"not an AST value or a string: $v")
      })
      case EConvert(source, target, flags) => ???
      case EContains(list, elem) => for {
        l <- transfer(list)
        v <- transfer(elem)
        b <- get(_.contains(l.escaped.loc, v.escaped))
      } yield AbsValue(bool = b)
      case EReturnIfAbrupt(rexpr @ ERef(ref), check) => for {
        rv <- transfer(ref)
        v <- transfer(rv)
        newV <- returnIfAbrupt(v, check)
        _ <- modify(_.update(rv, newV))
      } yield newV
      case EReturnIfAbrupt(expr, check) => for {
        v <- transfer(expr)
        newV <- returnIfAbrupt(v, check)
      } yield newV
      case copy @ ECopy(obj) => {
        val loc: AllocSite = AllocSite(copy.asite, cp.view)
        for {
          v <- transfer(obj)
          _ <- modify(_.copyObj(v.escaped.loc)(loc))
        } yield AbsValue(loc)
      }
      case keys @ EKeys(mobj, intSorted) => {
        val loc: AllocSite = AllocSite(keys.asite, cp.view)
        for {
          v <- transfer(mobj)
          _ <- modify(_.keys(v.escaped.loc, intSorted)(loc))
        } yield AbsValue(loc)
      }
      case ENotSupported(msg) => AbsValue.Bot
    }

    // return if abrupt completion
    def returnIfAbrupt(
      value: AbsValue,
      check: Boolean
    ): Result[AbsValue] = {
      val comp = value.comp
      val checkReturn: Result[Unit] =
        if (check) doReturn(AbsValue(comp = comp.removeNormal))
        else ()
      val newValue = comp.normal.value ⊔ value.pure
      for (_ <- checkReturn) yield newValue
    }

    // transfer function for references
    def transfer(ref: Ref): Result[AbsRefValue] = ref match {
      case RefId(id) => AbsRefId(id)
      case RefProp(ref, expr) => for {
        rv <- transfer(ref)
        b <- transfer(rv)
        p <- transfer(expr)
      } yield AbsRefProp(b, p.escaped)
    }

    // unary operators
    def transfer(
      st: AbsState,
      uop: UOp,
      operand: AbsValue
    ): AbsValue = operand.simple.getSingle match {
      case FlatBot => AbsValue.Bot
      case FlatElem(ASimple(x)) =>
        AbsValue(Interp.interp(uop, x))
      case FlatTop => uop match {
        case ONeg => ???
        case ONot => ???
        case OBNot => ???
      }
    }

    // binary operators
    def transfer(
      st: AbsState,
      bop: BOp,
      left: AbsValue,
      right: AbsValue
    ): AbsValue = (left.getSingle, right.getSingle) match {
      case (FlatBot, _) | (_, FlatBot) => AbsValue.Bot
      case (FlatElem(ASimple(l)), FlatElem(ASimple(r))) =>
        AbsValue(Interp.interp(bop, l, r))
      case (FlatElem(l), FlatElem(r)) if bop == OEq || bop == OEqual =>
        (l, r) match {
          case (lloc: Loc, rloc: Loc) => if (lloc == rloc) {
            if (st.isSingle(lloc)) AVT
            else AVB
          } else AVF
          case _ => AbsValue(l == r)
        }
      case _ => bop match {
        case OAnd => ???
        case OBAnd => ???
        case OBOr => ???
        case OBXOr => ???
        case ODiv => ???
        case OEq => ???
        case OEqual => ???
        case OLShift => ???
        case OLt => ???
        case OMod => ???
        case OMul => ???
        case OOr => ???
        case OPlus => ???
        case OPow => ???
        case OSRShift => ???
        case OSub => ???
        case OUMod => ???
        case OURShift => ???
        case OXor => ???
      }
    }

    // transfer function for reference values
    def transfer(rv: AbsRefValue): Result[AbsValue] = for {
      v <- get(_(rv, cp))
    } yield v

    // short circuit evaluation
    def shortCircuit(
      bop: BOp,
      left: Expr,
      right: Expr
    ): Result[AbsValue] = for {
      l <- transfer(left)
      b = l.escaped.bool
      v <- (bop, b.getSingle) match {
        case (OAnd, FlatElem(Bool(false))) => pure(AVF)
        case (OOr, FlatElem(Bool(true))) => pure(AVT)
        case _ => for {
          r <- transfer(right)
          v <- get(transfer(_, bop, l, r.escaped))
        } yield v
      }
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

  // simple functions
  type SimpleFunc = List[AbsValue] => Result[AbsValue]
  val simpleFuncs: Map[String, SimpleFunc] = {
    import AbsObj._
    Map(
      "GetArgument" -> {
        case List(v) => id(_.pop(v.loc, AbsValue(0)))
      },
      "IsDuplicate" -> {
        case List(v) => for {
          st <- get
        } yield AbsValue(bool = v.loc.foldLeft(AbsBool.Bot: AbsBool) {
          case (b, loc) => b ⊔ (st(loc) match {
            case ListElem(vs) if vs.forall(_.isSingle) => AbsBool(Bool((for {
              v <- vs
              av <- v.getSingle match {
                case FlatElem(av) => Some(av)
                case _ => None
              }
            } yield av).toSet.size == vs.length))
            case _ => AB
          })
        })
      },
      "IsArrayIndex" -> { case args => ??? },
      "min" -> { case args => ??? },
      "max" -> { case args => ??? },
      "abs" -> { case args => ??? },
      "floor" -> { case args => ??? },
      "fround" -> { case args => ??? },
      "ThrowCompletion" -> {
        case List(value) => value.wrapCompletion("throw")
      },
      "NormalCompletion" -> {
        case List(value) => pure(value.wrapCompletion)
      },
      "IsAbruptCompletion" -> {
        case List(value) => pure(AbsValue(bool = value.isAbruptCompletion))
      },
    )
  }
}
