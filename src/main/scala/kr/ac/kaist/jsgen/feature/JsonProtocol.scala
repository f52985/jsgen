package kr.ac.kaist.jsgen.feature

import io.circe._, io.circe.generic.semiauto._, io.circe.generic.auto._
import io.circe.syntax._
import kr.ac.kaist.jsgen.util.BasicJsonProtocol
import kr.ac.kaist.jsgen.util.Useful._

object JsonProtocol extends BasicJsonProtocol {
  implicit lazy val featureDecoder: Decoder[Feature] = deriveDecoder
  implicit lazy val featureEncoder: Encoder[Feature] = deriveEncoder

  implicit lazy val featureVectorDecoder: Decoder[FeatureVector] = deriveDecoder
  implicit lazy val featureVectorEncoder: Encoder[FeatureVector] = deriveEncoder
}
