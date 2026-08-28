package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Batch, Scan}
import org.apache.spark.sql.types.{StructField, StructType}

/**
 * Concrete implementation of Spark's connector [[Scan]] for ARCGIS datasource
 *
 * @param readConfig  options that control how the feature layer is queried
 * @param tableSchema Spark schema of the scan
 * @since 0.1.0
 */
class ArcgisScan(
                  private val readConfig: ReadConfig,
                  private val tableSchema: StructType
                )
  extends Scan {

  /**
   * Returns the Spark schema of this scan.
   *
   * @return the table schema
   * @since 0.1.0
   */
  override def readSchema(): StructType = tableSchema

  /**
   * Converts this scan into a batch scan.
   *
   * @return an [[ArcgisBatch]] for the configured read options
   * @since 0.1.0
   */
  override def toBatch: Batch = {

    new ArcgisBatch(readConfig)
  }
}
