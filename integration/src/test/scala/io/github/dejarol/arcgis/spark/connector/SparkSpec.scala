package io.github.dejarol.arcgis.spark.connector

import org.apache.spark.sql.SparkSession

/**
 * TODO
 */
trait SparkSpec {

  protected final lazy val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName(classOf[SparkSpec].getSimpleName)
    .getOrCreate()
}
