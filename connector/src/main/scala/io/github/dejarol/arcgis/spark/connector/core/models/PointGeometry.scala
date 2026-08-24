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
                         override val spatialReference: SpatialReference
                       )
  extends Geometry(spatialReference)
