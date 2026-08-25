package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * TODO
 */
trait Geometry {

  val spatialReference: Option[SpatialReference]

  /**
   * TODO
   * @return
   */
  def `type`(): EsriGeometryType

  /**
   * TODO
   * @return
   */
  def isAPoint: Boolean

  /**
   * TODO
   * @return
   */
  def isAPolygon: Boolean
}
