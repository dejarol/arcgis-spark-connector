package io.github.dejarol.arcgis.spark.connector.core.json

import io.github.dejarol.arcgis.spark.connector.core.EnumWithAPIName
import io.github.dejarol.arcgis.spark.connector.core.utils.Enums
import org.json4s.{CustomSerializer, JString}

import scala.reflect.ClassTag

/**
 * Provides JSON4s serializers for ArcGIS-specific value types.
 */
object Customizations {

  /**
   * Creates a serializer that maps an enum to and from its ArcGIS API name.
   *
   * @tparam E enum type exposing an ArcGIS API name
   * @return a serializer for the specified enum type
   */
  def serializerForEnumWithAPIName[E <: Enum[E] with EnumWithAPIName: ClassTag](): CustomSerializer[E] = {

    new CustomSerializer[E](_ => (
      {
        case JString(s) if Enums.exists[E](s, (v: E, n: String) => v.matchesAPIName(n)) =>
          Enums.unsafeValueOfEnum[E](s, (v: E, n: String) => v.matchesAPIName(n))
      },
      {
        case e: E => JString(e.getAPIName)
      }
    ))
  }
}
