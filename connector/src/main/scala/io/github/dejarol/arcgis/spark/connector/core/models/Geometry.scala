package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * ArcGIS geometry with an optional spatial reference.
 *
 * @since 0.1.0
 */
trait Geometry {

  val spatialReference: Option[SpatialReference]

  /**
   * Returns the ArcGIS geometry type of this instance.
   *
   * @return the [[EsriGeometryType]] of this geometry
   * @since 0.1.0
   */
  def `type`(): EsriGeometryType

  /**
   * Reports whether this geometry is a point.
   *
   * @return `true` if this geometry is a [[PointGeometry]], `false` otherwise
   * @since 0.1.0
   */
  def isAPoint: Boolean

  /**
   * Reports whether this geometry is a polygon.
   *
   * @return `true` if this geometry is a [[PolygonGeometry]], `false` otherwise
   * @since 0.1.0
   */
  def isAPolygon: Boolean
}
