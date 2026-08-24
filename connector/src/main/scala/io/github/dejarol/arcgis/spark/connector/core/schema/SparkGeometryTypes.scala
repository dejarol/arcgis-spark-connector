package io.github.dejarol.arcgis.spark.connector.core.schema

import io.github.dejarol.arcgis.spark.connector.core.models.{PointGeometry, PolygonGeometry, SpatialReference}
import org.apache.spark.sql.Encoders
import org.apache.spark.sql.types.StructType

/**
 * Spark SQL struct types that represent ArcGIS geometries.
 *
 * @since 0.1.0
 */
object SparkGeometryTypes {

  /**
   * Spark schema of an ArcGIS spatial reference.
   *
   * @since 0.1.0
   */
  final val SPATIAL_REFERENCE_TYPE: StructType = Encoders.product[SpatialReference].schema

  /**
   * Spark schema of an ArcGIS point geometry.
   *
   * @since 0.1.0
   */
  final val POINT: StructType = Encoders.product[PointGeometry].schema

  /**
   * Spark schema of an ArcGIS polygon geometry.
   *
   * @since 0.1.0
   */
  final val POLYGON: StructType = Encoders.product[PolygonGeometry].schema
}
