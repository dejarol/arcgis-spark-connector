package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * TODO
 *
 * @param rings
 * @param spatialReference
 */
case class PolygonGeometry(
                            rings: Seq[Seq[Seq[Double]]],
                            override val spatialReference: SpatialReference
                          )
  extends Geometry(spatialReference)

object PolygonGeometry {

}
