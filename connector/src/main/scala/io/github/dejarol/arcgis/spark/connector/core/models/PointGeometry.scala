package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * TODO
 * @param x
 * @param y
 * @param spatialReference
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
