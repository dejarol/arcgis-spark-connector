package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Batch, Scan}
import org.apache.spark.sql.types.StructType

/**
 * Concrete implementation of Spark's connector [[org.apache.spark.sql.connector.read.Scan]] for ARCGIS datasource
 *
 * @param scanConfig  options that control how the feature layer is queried
 * @param scanSchema Spark schema of the scan
 * @since 0.1.0
 */
class ArcgisScan(
                  private val scanConfig: ReadConfig,
                  private val scanSchema: StructType
                )
  extends Scan {

  /**
   * Returns the read options used by this scan.
   *
   * @return the scan configuration
   * @since 0.2.0
   */
  def config: ReadConfig = scanConfig

  /**
   * Returns the Spark schema of this scan.
   *
   * @return the table schema
   * @since 0.1.0
   */
  override def readSchema(): StructType = scanSchema

  /**
   * Converts this scan into a batch scan.
   *
   * @return an [[ArcgisBatch]] for the configured read options
   * @since 0.1.0
   */
  override def toBatch: Batch = {

    new ArcgisBatch(scanConfig)
  }
}
