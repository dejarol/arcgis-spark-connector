package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * ArcGIS geometry with an optional spatial reference.
 *
 * @since 0.1.0
 */
trait Geometry {

  val spatialReference: Option[SpatialReference]
}
