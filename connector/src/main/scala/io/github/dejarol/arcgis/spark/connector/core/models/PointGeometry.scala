package io.github.dejarol.arcgis.spark.connector.core.models

import io.github.dejarol.arcgis.spark.connector.core.json.Json4SUtils
import org.json4s.{DefaultFormats, JObject, JValue}

/**
 * ArcGIS point geometry with optional spatial reference.
 *
 * @param x                x coordinate
 * @param y                y coordinate
 * @param spatialReference optional spatial reference of the point
 * @since 0.1.0
 */
case class PointGeometry(
                         x: Double,
                         y: Double,
                         spatialReference: Option[SpatialReference]
                       )
  extends Geometry {
}

object PointGeometry
  extends FromJSON[PointGeometry] {

  override def fromJSON(json: JValue): PointGeometry = {

    json match {
      case obj: JObject if isPointGeometryJson(obj) => asPointGeometry(obj)
      case _ => throw exceptionForGeometryType(EsriGeometryType.POINT)
    }
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
    fields.get("x").exists(Json4SUtils.isNumber) &&
      fields.get("y").exists(Json4SUtils.isNumber)
  }

  /**
   * Deserializes the input object as a [[PointGeometry]]
   *
   * @param json the input JSON
   * @return a [[PointGeometry]]
   * @since 0.1.0
   */
  private def asPointGeometry(json: JObject): PointGeometry = {

   json.extract[PointGeometry](
     DefaultFormats, manifest[PointGeometry]
   )
  }
}
