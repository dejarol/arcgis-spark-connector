package io.github.dejarol.arcgis.spark.connector

import org.apache.spark.sql.SparkSession

/**
 * Mix-in trait for Spark integration tests.
 */
trait SparkMixins {

  protected final lazy val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName(classOf[SparkMixins].getSimpleName)
    .getOrCreate()
}
