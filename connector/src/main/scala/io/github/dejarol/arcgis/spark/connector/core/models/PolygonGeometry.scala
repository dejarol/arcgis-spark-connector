package io.github.dejarol.arcgis.spark.connector.core.models

import io.github.dejarol.arcgis.spark.connector.core.json.Json4SUtils
import org.json4s.JsonAST.{JArray, JField, JObject}
import org.json4s.{DefaultFormats, JValue}

/**
 * ArcGIS polygon geometry defined by nested rings and an optional spatial reference.
 *
 * @param rings            nested ring coordinates (polygons of points of x/y values)
 * @param spatialReference optional spatial reference of the polygon
 * @since 0.1.0
 */
case class PolygonGeometry(
                            rings: Seq[Seq[Seq[Double]]],
                            spatialReference: Option[SpatialReference]
                          )
  extends Geometry {
}

object PolygonGeometry
  extends FromJSON[PolygonGeometry] {

  override def fromJSON(json: JValue): PolygonGeometry = {

    json match {
      case obj: JObject if isPolygonGeometryJson(obj) => asPolygonGeometry(obj)
      case _ => throw exceptionForGeometryType(EsriGeometryType.POLYGON)
    }
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
              case JArray(coordinates) => coordinates.forall(Json4SUtils.isNumber)
              case _ => false
            }
          case _ => false
        }
      case _ => false
    }
  }

  /**
   * Converts a JSON object into a [[PolygonGeometry]].
   *
   * @param json the input JSON
   * @return a [[PolygonGeometry]]
   * @since 0.1.0
   */
  private def asPolygonGeometry(json: JObject): PolygonGeometry = {

    json.extract[PolygonGeometry](
      DefaultFormats, manifest[PolygonGeometry]
    )
  }
}
