package io.github.dejarol.arcgis.spark.connector.core.schema

import org.apache.spark.sql.types.{DataTypes, DoubleType, IntegerType, StructField, StructType}

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
  final val SPATIAL_REFERENCE_TYPE: StructType = StructType(
    Seq(
      StructField("wkid", IntegerType),
      StructField("latestWkid", IntegerType)
    )
  )

  /**
   * Spark schema of an ArcGIS point geometry.
   *
   * @since 0.1.0
   */
  final val POINT: StructType = StructType(
    Seq(
      StructField("x", DoubleType),
      StructField("y", DoubleType),
      StructField("spatialReference", SPATIAL_REFERENCE_TYPE)
    )
  )

  /**
   * Spark schema of an ArcGIS polygon geometry.
   *
   * @since 0.1.0
   */
  final val POLYGON: StructType = StructType(
    Seq(
      StructField("rings",
        DataTypes.createArrayType(
          DataTypes.createArrayType(
            DataTypes.createArrayType(DoubleType)
          )
        )
      ),
      StructField("spatialReference", SPATIAL_REFERENCE_TYPE)
    )
  )
}
