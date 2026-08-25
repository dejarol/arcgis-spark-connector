package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * TODO
 *
 * @param rings
 * @param spatialReference
 */
case class PolygonGeometry(
                            rings: Seq[Seq[Seq[Double]]],
                            spatialReference: Option[SpatialReference]
                          )
  extends Geometry {

  override def `type`(): EsriGeometryType = EsriGeometryType.POLYGON

  override def isAPoint: Boolean = false

  override def isAPolygon: Boolean = true

  /**
   * TODO
   * @return
   */
  def numberOfPolygons: Int = rings.size

  /**
   * TODO
   * @return
   */
  def numberOfVertices: Int = rings.flatten.size
}
