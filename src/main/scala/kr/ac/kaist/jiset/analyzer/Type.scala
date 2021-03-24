package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir.doubleEquals
import kr.ac.kaist.jiset.util.Useful._

sealed trait Type {
  // conversion to abstract type
  def abs: AbsType = AbsType(this)

  // get ancestor types
  def parent: Option[Type] = optional(this match {
    case NormalT(t) => t.parent match {
      case Some(parent: PureType) => NormalT(parent)
      case _ => error("no parent")
    }
    case NameT(name) => ???
    case PrimT => ESValueT
    case NumericT => PrimT
    case NumT => NumericT
    case BigIntT => NumericT
    case StrT => PrimT
    case BoolT => PrimT
    case SymbolT => PrimT
    case Num(n) => NumT
    case BigInt(b) => BigIntT
    case Str(str) => StrT
    case Bool(b) => BoolT
    case Undef => PrimT
    case Null => PrimT
    case _ => error("no parent")
  })

  // conversion to completions
  def toComp: CompType = this match {
    case (t: PureType) => NormalT(t)
    case (t: CompType) => t
  }

  // escape completions
  def escaped: Option[PureType] = this match {
    case (t: PureType) => Some(t)
    case NormalT(t) => Some(t)
    case AbruptT =>
      alarm(s"Unchecked abrupt completions")
      None
  }

  // conversion to string
  override def toString: String = this match {
    case NameT(name) => s"$name"
    case AstT(name) => s"☊($name)"
    case ConstT(name) => s"~$name~"
    case CloT(fid) => s"λ[$fid]"
    case ESValueT => s"ESValue"
    case PrimT => "prim"
    case NumericT => "numeric"
    case NumT => "num"
    case BigIntT => "bigint"
    case StrT => "str"
    case BoolT => "bool"
    case NilT => s"[]"
    case ListT(elem) => s"[$elem]"
    case MapT(elem) => s"{ _ |-> $elem }"
    case SymbolT => "symbol"
    case NormalT(t) => s"Normal($t)"
    case AbruptT => s"Abrupt"
    case Num(n) => s"$n"
    case BigInt(b) => s"${b}n"
    case Str(str) => "\"" + str + "\""
    case Bool(b) => s"$b"
    case Undef => "undef"
    case Null => "null"
    case Absent => "?"
  }
}
object Type {
  // information
  case class Info(
    name: String,
    parent: Option[String],
    props: Map[String, AbsType]
  )
  object Info {
    // constructor
    def apply(name: String, props: Map[String, AbsType]): Info =
      Info(name, None, props)
    def apply(name: String, parent: String, props: Map[String, AbsType]): Info =
      Info(name, Some(parent), props)
  }

  // property map
  type PropMap = Map[String, AbsType]

  // get type information
  lazy val all: List[Info] = List(
    // realm records
    Info("RealmRecord", Map(
      "Intrinsics" -> MapT(NameT("OrdinaryObject")),
      "GlobalObject" -> NameT("OrdinaryObject"),
      "GlobalEnv" -> NameT("GlobalEnvironmentRecord"),
      "TemplateMap" -> ListT(NameT("TemplatePair")),
      "HostDefined" -> Undef,
    )),
    Info("TemplatePair", Map(
      "Site" -> AstT("TemplateLiteral"),
      "Array" -> NameT("Object"),
    )),

    // property descriptors
    Info("PropertyDescriptor", Map(
      "Value" -> AbsType(ESValueT, Absent),
      "Writable" -> AbsType(BoolT, Absent),
      "Get" -> AbsType(NameT("FunctionObject"), Undef, Absent),
      "Set" -> AbsType(NameT("FunctionObject"), Undef, Absent),
      "Enumerable" -> AbsType(BoolT, Absent),
      "Configurable" -> AbsType(BoolT, Absent),
    )),

    // objects
    Info("Object", Map(
      "SubMap" -> MapT(NameT("PropertyDescriptor")),
      "Prototype" -> AbsType(NameT("Object"), Null),
      "Extensible" -> BoolT,
      "GetPrototypeOf" -> getClo("OrdinaryObject.GetPrototypeOf"),
      "SetPrototypeOf" -> getClo("OrdinaryObject.SetPrototypeOf"),
      "IsExtensible" -> getClo("OrdinaryObject.IsExtensible"),
      "PreventExtensions" -> getClo("OrdinaryObject.PreventExtensions"),
      "GetOwnProperty" -> getClo("OrdinaryObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("OrdinaryObject.DefineOwnProperty"),
      "HasProperty" -> getClo("OrdinaryObject.HasProperty"),
      "Get" -> getClo("OrdinaryObject.Get"),
      "Set" -> getClo("OrdinaryObject.Set"),
      "Delete" -> getClo("OrdinaryObject.Delete"),
      "OwnPropertyKeys" -> getClo("OrdinaryObject.OwnPropertyKeys"),
    )),
    Info("OrdinaryObject", parent = "Object", Map()),
    Info("FunctionObject", parent = "OrdinaryObject", Map()),
    Info("ECMAScriptFunctionObject", parent = "FunctionObject", Map(
      "Environment" -> NameT("EnvironmentRecord"),
      "FormalParameters" -> AstT("FormalParameters"),
      "ECMAScriptCode" -> AstT("FunctionBody"),
      "ConstructorKind" -> AbsType(BASE, DERIVED),
      "Realm" -> NameT("RealmRecord"),
      "ScriptOrModule" -> AbsType(NameT("ScriptRecord"), NameT("ModuleRecord")),
      "ThisMode" -> AbsType(LEXICAL, STRICT, GLOBAL),
      "Strict" -> BoolT,
      "HomeObject" -> NameT("Object"),
      "SourceText" -> StrT,
      "IsClassConstructor" -> BoolT,
      "Call" -> getClo("ECMAScriptFunctionObject.Call"),
      "Construct" -> getClo("ECMAScriptFunctionObject.Construct"),
    )),
    Info("BuiltinFunctionObject", parent = "FunctionObject", Map(
      "InitialName" -> AbsType(StrT, Null),
      "Call" -> getClo("BuiltinFunctionObject.Call"),
      "Construct" -> getClo("BuiltinFunctionObject.Construct"),
      "Realm" -> NameT("RealmRecord"),
    )),
    Info("BoundFunctionExoticObject", parent = "Object", Map(
      "BoundTargetFunction" -> NameT("FunctionObject"),
      "BoundThis" -> ESValueT,
      "BoundArguments" -> ListT(ESValueT),
      "Call" -> getClo("BoundFunctionExoticObject.Call"),
      "Construct" -> getClo("BoundFunctionExoticObject.Construct"),
    )),
    Info("ArrayExoticObject", parent = "Object", Map(
      "DefineOwnProperty" -> getClo("ArrayExoticObject.DefineOwnProperty"),
    )),
    Info("StringExoticObject", parent = "Object", Map(
      "GetOwnProperty" -> getClo("StringExoticObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("StringExoticObject.DefineOwnProperty"),
      "OwnPropertyKeys" -> getClo("StringExoticObject.OwnPropertyKeys"),
    )),
    Info("ArgumentsExoticObject", parent = "Object", Map(
      "GetOwnProperty" -> getClo("ArgumentsExoticObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("ArgumentsExoticObject.DefineOwnProperty"),
      "Get" -> getClo("ArgumentsExoticObject.Get"),
      "Set" -> getClo("ArgumentsExoticObject.Set"),
      "Delete" -> getClo("ArgumentsExoticObject.Delete"),
    )),
    Info("IntegerIndexedExoticObject", parent = "Object", Map(
      "GetOwnProperty" -> getClo("IntegerIndexedExoticObject.GetOwnProperty"),
      "HasProperty" -> getClo("IntegerIndexedExoticObject.HasProperty"),
      "DefineOwnProperty" -> getClo("IntegerIndexedExoticObject.DefineOwnProperty"),
      "Get" -> getClo("IntegerIndexedExoticObject.Get"),
      "Set" -> getClo("IntegerIndexedExoticObject.Set"),
      "Delete" -> getClo("IntegerIndexedExoticObject.Delete"),
      "OwnPropertyKeys" -> getClo("IntegerIndexedExoticObject.OwnPropertyKeys"),
    )),
    Info("ModuleNamespaceExoticObject", parent = "Object", Map(
      "Module" -> NameT("ModuleRecord"),
      "Exports" -> ListT(StrT),
      "Prototype" -> Null,
      "SetPrototypeOf" -> getClo("ModuleNamespaceExoticObject.SetPrototypeOf"),
      "IsExtensible" -> getClo("ModuleNamespaceExoticObject.IsExtensible"),
      "PreventExtensions" -> getClo("ModuleNamespaceExoticObject.PreventExtensions"),
      "GetOwnProperty" -> getClo("ModuleNamespaceExoticObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("ModuleNamespaceExoticObject.DefineOwnProperty"),
      "HasProperty" -> getClo("ModuleNamespaceExoticObject.HasProperty"),
      "Get" -> getClo("ModuleNamespaceExoticObject.Get"),
      "Set" -> getClo("ModuleNamespaceExoticObject.Set"),
      "Delete" -> getClo("ModuleNamespaceExoticObject.Delete"),
      "OwnPropertyKeys" -> getClo("ModuleNamespaceExoticObject.OwnPropertyKeys"),
    )),
    Info("ImmutablePrototypeExoticObject", parent = "Object", Map(
      "SetPrototypeOf" -> getClo("ImmutablePrototypeExoticObject.SetPrototypeOf"),
    )),
    Info("ProxyObject", parent = "Object", Map(
      "GetPrototypeOf" -> getClo("ProxyObject.GetPrototypeOf"),
      "SetPrototypeOf" -> getClo("ProxyObject.SetPrototypeOf"),
      "IsExtensible" -> getClo("ProxyObject.IsExtensible"),
      "PreventExtensions" -> getClo("ProxyObject.PreventExtensions"),
      "GetOwnProperty" -> getClo("ProxyObject.GetOwnProperty"),
      "DefineOwnProperty" -> getClo("ProxyObject.DefineOwnProperty"),
      "HasProperty" -> getClo("ProxyObject.HasProperty"),
      "Get" -> getClo("ProxyObject.Get"),
      "Set" -> getClo("ProxyObject.Set"),
      "Delete" -> getClo("ProxyObject.Delete"),
      "OwnPropertyKeys" -> getClo("ProxyObject.OwnPropertyKeys"),
      "Call" -> getClo("ProxyObject.Call"),
      "Construct" -> getClo("ProxyObject.Construct"),
    )),

    // reference records
    Info("ReferenceRecord", Map(
      "Value" -> AbsType(ESValueT, NameT("EnvironmentRecord"), UNRESOLVABLE),
      "ReferencedName" -> AbsType(StrT, SymbolT),
      "Strict" -> BoolT,
      "ThisValue" -> AbsType(ESValueT, EMPTY),
    )),

    // environment records
    Info("EnvironmentRecord", Map(
      "SubMap" -> MapT(NameT("Binding")),
    )),
    Info("DeclarativeEnvironmentRecord", parent = "EnvironmentRecord", Map(
      "HasBinding" -> getClo("DeclarativeEnvironmentRecord.HasBinding"),
      "CreateMutableBinding" -> getClo("DeclarativeEnvironmentRecord.CreateMutableBinding"),
      "CreateImmutableBinding" -> getClo("DeclarativeEnvironmentRecord.CreateImmutableBinding"),
      "InitializeBinding" -> getClo("DeclarativeEnvironmentRecord.InitializeBinding"),
      "SetMutableBinding" -> getClo("DeclarativeEnvironmentRecord.SetMutableBinding"),
      "GetBindingValue" -> getClo("DeclarativeEnvironmentRecord.GetBindingValue"),
      "DeleteBinding" -> getClo("DeclarativeEnvironmentRecord.DeleteBinding"),
      "HasThisBinding" -> getClo("DeclarativeEnvironmentRecord.HasThisBinding"),
      "HasSuperBinding" -> getClo("DeclarativeEnvironmentRecord.HasSuperBinding"),
      "WithBaseObject" -> getClo("DeclarativeEnvironmentRecord.WithBaseObject"),
    )),
    Info("ObjectEnvironmentRecord", parent = "EnvironmentRecord", Map(
      "BindingObject" -> NameT("Object"),
      "HasBinding" -> getClo("ObjectEnvironmentRecord.HasBinding"),
      "CreateMutableBinding" -> getClo("ObjectEnvironmentRecord.CreateMutableBinding"),
      "InitializeBinding" -> getClo("ObjectEnvironmentRecord.InitializeBinding"),
      "SetMutableBinding" -> getClo("ObjectEnvironmentRecord.SetMutableBinding"),
      "GetBindingValue" -> getClo("ObjectEnvironmentRecord.GetBindingValue"),
      "DeleteBinding" -> getClo("ObjectEnvironmentRecord.DeleteBinding"),
      "HasThisBinding" -> getClo("ObjectEnvironmentRecord.HasThisBinding"),
      "HasSuperBinding" -> getClo("ObjectEnvironmentRecord.HasSuperBinding"),
      "WithBaseObject" -> getClo("ObjectEnvironmentRecord.WithBaseObject"),
    )),
    Info("FunctionEnvironmentRecord", parent = "DeclarativeEnvironmentRecord", Map(
      "ThisValue" -> ESValueT,
      "ThisBindingStatus" -> AbsType(LEXICAL, INITIALIZED, UNINITIALIZED),
      "FunctionObject" -> NameT("Object"),
      "NewTarget" -> AbsType(NameT("Object"), Undef),
      "BindThisValue" -> getClo("FunctionEnvironmentRecord.BindThisValue"),
      "HasThisBinding" -> getClo("FunctionEnvironmentRecord.HasThisBinding"),
      "HasSuperBinding" -> getClo("FunctionEnvironmentRecord.HasSuperBinding"),
      "GetThisBinding" -> getClo("FunctionEnvironmentRecord.GetThisBinding"),
      "GetSuperBase" -> getClo("FunctionEnvironmentRecord.GetSuperBase"),
    )),
    Info("GlobalEnvironmentRecord", parent = "EnvironmentRecord", Map(
      "ObjectRecord" -> NameT("ObjectEnvironmentRecord"),
      "GlobalThisValue" -> NameT("Object"),
      "DeclarativeRecord" -> NameT("DeclarativeEnvironmentRecord"),
      "VarNames" -> ListT(StrT),
      "HasBinding" -> getClo("GlobalEnvironmentRecord.HasBinding"),
      "CreateMutableBinding" -> getClo("GlobalEnvironmentRecord.CreateMutableBinding"),
      "CreateImmutableBinding" -> getClo("GlobalEnvironmentRecord.CreateImmutableBinding"),
      "InitializeBinding" -> getClo("GlobalEnvironmentRecord.InitializeBinding"),
      "SetMutableBinding" -> getClo("GlobalEnvironmentRecord.SetMutableBinding"),
      "GetBindingValue" -> getClo("GlobalEnvironmentRecord.GetBindingValue"),
      "DeleteBinding" -> getClo("GlobalEnvironmentRecord.DeleteBinding"),
      "HasThisBinding" -> getClo("GlobalEnvironmentRecord.HasThisBinding"),
      "HasSuperBinding" -> getClo("GlobalEnvironmentRecord.HasSuperBinding"),
      "WithBaseObject" -> getClo("GlobalEnvironmentRecord.WithBaseObject"),
      "GetThisBinding" -> getClo("GlobalEnvironmentRecord.GetThisBinding"),
      "HasVarDeclaration" -> getClo("GlobalEnvironmentRecord.HasVarDeclaration"),
      "HasLexicalDeclaration" -> getClo("GlobalEnvironmentRecord.HasLexicalDeclaration"),
      "HasRestrictedGlobalProperty" -> getClo("GlobalEnvironmentRecord.HasRestrictedGlobalProperty"),
      "CanDeclareGlobalVar" -> getClo("GlobalEnvironmentRecord.CanDeclareGlobalVar"),
      "CanDeclareGlobalFunction" -> getClo("GlobalEnvironmentRecord.CanDeclareGlobalFunction"),
      "CreateGlobalVarBinding" -> getClo("GlobalEnvironmentRecord.CreateGlobalVarBinding"),
      "CreateGlobalFunctionBinding" -> getClo("GlobalEnvironmentRecord.CreateGlobalFunctionBinding"),
    )),

    // execution contexts
    Info("ExecutionContext", Map(
      "Function" -> AbsType(NameT("FunctionObject"), Null),
      "Realm" -> NameT("RealmRecord"),
      "ScriptOrModule" -> AbsType(NameT("ScriptRecord"), NameT("ModuleRecord")),
      "LexicalEnvironment" -> NameT("EnvironmentRecord"),
      "VariableEnvironment" -> NameT("EnvironmentRecord"),
      "Generator" -> NameT("Object"),
    )),

    // job callback records
    Info("JobCallbackRecord", Map(
      "Callback" -> NameT("FunctionObject"),
      "HostDefined" -> EMPTY,
    )),

    // agent records
    Info("AgentRecord", Map(
      "LittleEndian" -> BoolT,
      "CanBlock" -> BoolT,
      "Signifier" -> Undef,
      "IsLockFree1" -> BoolT,
      "IsLockFree2" -> BoolT,
      "IsLockFree3" -> BoolT,
      "CandidateExecution" -> NameT("CandidateExecutionRecord"),
      "KeptAlive" -> ListT(NameT("Object")),
    )),

    // script records
    Info("ScriptRecord", Map(
      "Realm" -> AbsType(NameT("RealmRecord"), Undef),
      "Environment" -> AbsType(NameT("EnvironmentRecord"), Undef),
      "ECMAScriptCode" -> AstT("Script"),
      "HostDefined" -> EMPTY,
    )),

    // module record
    Info("ModuleRecord", Map(
      "Realm" -> AbsType(NameT("RealmRecord"), Undef),
      "Environment" -> AbsType(NameT("ModuleEnvironmentRecord"), Undef),
      "Namespace" -> AbsType(NameT("Object"), Undef),
      "HostDefined" -> EMPTY,
    )),
    Info("CyclicModuleRecord", parent = "ModuleRecord", Map(
      "Status" -> AbsType(UNLINKED, LINKING, LINKED, EVALUATING, EVALUATED),
      "EvaluationError" -> AbsType(AbruptT, Undef),
      "DFSIndex" -> AbsType(NumT, Undef),
      "DFSAncestorIndex" -> AbsType(NumT, Undef),
      "RequestedModules" -> ListT(StrT),
    )),
    Info("SourceTextModuleRecord", parent = "CyclicModuleRecord", Map(
      "ECMAScriptCode" -> AstT("Module"),
      "Context" -> NameT("ExecutionContext"),
      "ImportMeta" -> AbsType(NameT("Object"), EMPTY),
      "ImportEntries" -> ListT(NameT("ImportEntryRecord")),
      "LocalExportEntries" -> ListT(NameT("ExportEntryRecord")),
      "IndirectExportEntries" -> ListT(NameT("ExportEntryRecord")),
      "StarExportEntries" -> ListT(NameT("ExportEntryRecord")),
    )),
    Info("ImportEntryRecord", Map(
      "ModuleRequest" -> StrT,
      "ImportName" -> StrT,
      "LocalName" -> StrT,
    )),
    Info("ExportEntryRecord", Map(
      "ExportName" -> AbsType(StrT, Null),
      "ModuleRequest" -> AbsType(StrT, Null),
      "ImportName" -> AbsType(StrT, Null),
      "LocalName" -> AbsType(StrT, Null),
    )),
  )

  // type info map
  lazy val totalMap: Map[String, Info] =
    all.map(info => info.name -> info).toMap

  // sub types
  lazy val subTypes: Map[String, Set[String]] = {
    var children = Map[String, Set[String]]()
    for {
      info <- all
      parent <- info.parent
      set = children.getOrElse(parent, Set())
    } children += parent -> (set + info.name)
    children
  }

  // property map
  lazy val propMap: Map[String, PropMap] =
    all.map(info => info.name -> getPropMap(info.name)).toMap

  //////////////////////////////////////////////////////////////////////////////
  // Private Helper Functions
  //////////////////////////////////////////////////////////////////////////////
  // get property map
  private def getPropMap(name: String): PropMap = {
    val upper = getUpperPropMap(name)
    val lower = getLowerPropMap(name)
    lower.foldLeft(upper) {
      case (map, (k, t)) =>
        val newT = t ⊔ map.getOrElse(k, AbsType.Bot)
        map + (k -> newT)
    }
  }

  // get property map from ancestors
  private def getUpperPropMap(name: String): PropMap = totalMap.get(name) match {
    case Some(info) => info.parent.map(getUpperPropMap).getOrElse(Map()) ++ info.props
    case None => Map()
  }

  // get property map from ancestors
  private def getLowerPropMap(name: String): PropMap = subTypes
    .getOrElse(name, Set())
    .map(child => {
      val lower = getLowerPropMap(child)
      val props = totalMap.get(child).map(_.props).getOrElse(Map())
      lower ++ props
    })
    .reduceOption(weakMerge)
    .getOrElse(Map())

  // weak merge
  private def weakMerge(lmap: PropMap, rmap: PropMap): PropMap = {
    val keys = lmap.keySet ++ rmap.keySet
    keys.toList.map(k => {
      k -> (lmap.getOrElse(k, Absent.abs) ⊔ rmap.getOrElse(k, Absent.abs))
    }).toMap
  }

  // get function closure by name
  private lazy val cloMap: Map[String, AbsType] =
    (for (func <- cfg.funcs) yield func.name -> CloT(func.uid).abs).toMap
  private def getClo(name: String): AbsType = cloMap.getOrElse(name, {
    alarm(s"unknown function name: $name")
    AbsType.Bot
  })
}

// completion types
sealed trait CompType extends Type
case class NormalT(value: PureType) extends CompType
case object AbruptT extends CompType

// pure types
sealed trait PureType extends Type

// ECMAScript value types
case object ESValueT extends PureType

// norminal types
case class NameT(name: String) extends PureType {
  // lookup propertys
  def apply(prop: String): AbsType =
    Type.propMap.getOrElse(name, Map()).getOrElse(prop, Absent)
}

// AST types
case class AstT(name: String) extends PureType

// constant types
case class ConstT(name: String) extends PureType

// closure types
case class CloT(fid: Int) extends PureType

// list types
case object NilT extends PureType
case class ListT(elem: PureType) extends PureType

// sub mapping types
case class MapT(elem: PureType) extends PureType

// symbol types
case object SymbolT extends PureType

// primitive types
case object PrimT extends PureType
case object NumericT extends PureType
case object NumT extends PureType
case object BigIntT extends PureType
case object StrT extends PureType
case object BoolT extends PureType

// single concrete type
sealed trait SingleT extends PureType {
  // upcast
  def upcast: PureType = this match {
    case Num(_) => NumT
    case BigInt(_) => BigIntT
    case Str(_) => StrT
    case Bool(_) => BoolT
    case Undef => Undef
    case Null => Null
    case Absent => Absent
  }
}
case class Num(double: Double) extends SingleT {
  override def equals(that: Any): Boolean = that match {
    case that: Num => doubleEquals(this.double, that.double)
    case _ => false
  }
}
case class BigInt(bigint: scala.BigInt) extends SingleT
case class Str(str: String) extends SingleT
case class Bool(bool: Boolean) extends SingleT
case object Undef extends SingleT
case object Null extends SingleT
case object Absent extends SingleT
