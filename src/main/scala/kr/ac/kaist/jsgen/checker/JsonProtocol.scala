package kr.ac.kaist.jsgen.checker

import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._
import kr.ac.kaist.jsgen.checker.Stringifier._
import kr.ac.kaist.jsgen.cfg._
import kr.ac.kaist.jsgen.ir.JsonProtocol._
import kr.ac.kaist.jsgen.ir._
import kr.ac.kaist.jsgen.util.BasicJsonProtocol
import kr.ac.kaist.jsgen.util.Useful._

class JsonProtocol(cfg: CFG) extends BasicJsonProtocol {
  import cfg.jsonProtocol._

  implicit lazy val absSemanticsDecoder: Decoder[AbsSemantics] = deriveDecoder
  implicit lazy val absSemanticsEncoder: Encoder[AbsSemantics] = deriveEncoder

  implicit lazy val controlPointDecoder: Decoder[ControlPoint] = new Decoder[ControlPoint] {
    final def apply(c: HCursor): Decoder.Result[ControlPoint] = {
      val obj = c.value.asObject.get
      val discrimator = List("node", "func").map(obj.contains(_))
      discrimator.indexOf(true) match {
        case 0 => nodePointDecoder(NodeDecoder)(c)
        case 1 => returnPointDecoder(c)
        case _ => decodeFail(s"invalid control point: $obj", c)
      }
    }
  }
  implicit lazy val controlPointEncoder: Encoder[ControlPoint] = Encoder.instance {
    case t: NodePoint[_] => nodePointEncoder(NodeEncoder)(t)
    case t: ReturnPoint => returnPointEncoder(t)
  }

  implicit def nodePointDecoder[T <: Node](
    implicit
    TDecoder: Decoder[T]
  ): Decoder[NodePoint[T]] = deriveDecoder
  implicit def nodePointEncoder[T <: Node](
    implicit
    TEncoder: Encoder[T]
  ): Encoder[NodePoint[T]] = deriveEncoder

  implicit lazy val returnPointDecoder: Decoder[ReturnPoint] = deriveDecoder
  implicit lazy val returnPointEncoder: Encoder[ReturnPoint] = deriveEncoder

  implicit lazy val viewDecoder: Decoder[View] = deriveDecoder
  implicit lazy val viewEncoder: Encoder[View] = deriveEncoder

  implicit lazy val absStateDecoder: Decoder[AbsState] = deriveDecoder
  implicit lazy val absStateEncoder: Encoder[AbsState] = deriveEncoder

  implicit lazy val (
    absTypeDecoder: Decoder[AbsType],
    absTypeEncoder: Encoder[AbsType]
    ) = stringCodec[AbsType](AbsType.apply, stringify)

  implicit lazy val (
    typeDecoder: Decoder[Type],
    typeEncoder: Encoder[Type]
    ) = stringCodec[Type](Type.apply, stringify)

  implicit lazy val visitRecorderDecoder: Decoder[VisitRecorder] = deriveDecoder
  implicit lazy val visitRecorderEncoder: Encoder[VisitRecorder] = deriveEncoder

  implicit lazy val cfgPartialModelDecoder: Decoder[CFGPartialModel] = deriveDecoder
  implicit lazy val cfgPartialModelEncoder: Encoder[CFGPartialModel] = deriveEncoder

  implicit lazy val partialFuncDecoder: Decoder[PartialFunc] = deriveDecoder
  implicit lazy val partialFuncEncoder: Encoder[PartialFunc] = deriveEncoder
}
