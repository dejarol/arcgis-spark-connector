package io.github.dejarol.arcgis.spark.connector.core.json

import io.github.dejarol.arcgis.spark.connector.core.EnumWithAPIName
import io.github.dejarol.arcgis.spark.connector.core.models.{Geometry, PointGeometry, PolygonGeometry}
import io.github.dejarol.arcgis.spark.connector.core.utils.Enums
import org.json4s.JsonAST.{JDecimal, JDouble, JInt, JLong, JObject}
import org.json4s.{CustomSerializer, DefaultFormats, Extraction, JArray, JField, JString, JValue}

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

  /**
   * Creates a serializer that maps ArcGIS geometry JSON to [[Geometry]] subtypes.
   *
   * A JSON object with `x`, `y`, and `spatialReference` is read as [[PointGeometry]].
   * A JSON object whose `rings` field is an array of arrays of arrays of numbers is read as [[PolygonGeometry]].
   *
   * @return a serializer for [[Geometry]]
   * @since 0.1.0
   */
  def serializerForGeometry(): CustomSerializer[Geometry] = {

    new CustomSerializer[Geometry](format => (
      {
        case json: JObject if isPointGeometryJson(json) =>
          json.extract[PointGeometry](format, manifest[PointGeometry])
        case json: JObject if isPolygonGeometryJson(json) =>
          json.extract[PolygonGeometry](format, manifest[PolygonGeometry])
      },
      {
        case point: PointGeometry =>
          Extraction.decompose(point)(DefaultFormats)
        case polygon: PolygonGeometry =>
          Extraction.decompose(polygon)(DefaultFormats)
      }
    ))
  }

  /**
   * Returns whether the JSON object looks like a point geometry.
   *
   * @param json JSON object to inspect
   * @return `true` when the object has numeric `x` and `y` fields plus `spatialReference`
   * @since 0.1.0
   */
  private def isPointGeometryJson(json: JObject): Boolean = {

    val fields = json.obj.toMap
    fields.get("x").exists(isJsonNumber) &&
      fields.get("y").exists(isJsonNumber) &&
      fields.contains("spatialReference")
  }

  /**
   * Returns whether the JSON object looks like a polygon geometry.
   *
   * @param json JSON object to inspect
   * @return `true` when `rings` is an array of arrays of arrays of numbers
   * @since 0.1.0
   */
  private def isPolygonGeometryJson(json: JObject): Boolean = {

    json.obj.exists {
      case JField("rings", rings) => isArrayOfArrayOfArrayOfNumbers(rings)
      case _ => false
    }
  }

  /**
   * Returns whether the value is a three-level nested JSON array of numbers.
   *
   * @param value JSON value to inspect
   * @return `true` when the value matches `number[][][]`
   * @since 0.1.0
   */
  private def isArrayOfArrayOfArrayOfNumbers(value: JValue): Boolean = {

    value match {
      case JArray(rings) =>
        rings.forall {
          case JArray(ring) =>
            ring.forall {
              case JArray(coordinates) => coordinates.forall(isJsonNumber)
              case _ => false
            }
          case _ => false
        }
      case _ => false
    }
  }

  /**
   * Returns whether the JSON value is a numeric node.
   *
   * @param value JSON value to inspect
   * @return `true` when the value is an integer, long, decimal, or double
   * @since 0.1.0
   */
  private def isJsonNumber(value: JValue): Boolean = {

    value match {
      case _: JDouble | _: JInt | _: JLong | _: JDecimal => true
      case _ => false
    }
  }
}
