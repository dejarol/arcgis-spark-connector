package io.github.dejarol.arcgis.spark.connector.core.models

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

  override def `type`(): EsriGeometryType = EsriGeometryType.POINT

  override def isAPoint: Boolean = true

  override def isAPolygon: Boolean = false
}
