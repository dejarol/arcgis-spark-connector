package io.github.dejarol.arcgis.spark.connector.core.models

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

  override def `type`(): EsriGeometryType = EsriGeometryType.POLYGON

  override def isAPoint: Boolean = false

  override def isAPolygon: Boolean = true

  /**
   * Returns the number of polygons in this geometry.
   *
   * @return the size of `rings`
   * @since 0.1.0
   */
  def numberOfPolygons: Int = rings.size

  /**
   * Returns the number of vertices in the polygon at the given index.
   *
   * @param index zero-based polygon index into `rings`
   * @return the number of vertices in that polygon
   * @since 0.1.0
   */
  def numberOfVerticesInPolygon(index: Int): Int = rings(index).size

  /**
   * Returns the total number of vertices across all polygons.
   *
   * @return the flattened size of `rings`
   * @since 0.1.0
   */
  def numberOfVertices: Int = rings.flatten.size
}
