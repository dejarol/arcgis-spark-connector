package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Scan, ScanBuilder, SupportsPushDownRequiredColumns}
import org.apache.spark.sql.types.StructType

/**
 * Concrete implementation of Spark's connector [[org.apache.spark.sql.connector.read.ScanBuilder]] for ARCGIS datasource
 *
 * Builds an [[ArcgisScan]] from the table schema and read configuration.
 *
 * @param readConfig  options that control how the feature layer is queried
 * @param tableSchema Spark schema of the table to scan
 * @since 0.1.0
 */
class ArcgisScanBuilder(
                         private val readConfig: ReadConfig,
                         private val tableSchema: StructType
                       )
  extends ScanBuilder
    with SupportsPushDownRequiredColumns {

  private var scanSchema = tableSchema
  private var scanConfig = readConfig

  override def pruneColumns(requiredSchema: StructType): Unit = {

    val requiredFieldNames = requiredSchema.fieldNames
      .toSet[String]
      .map(_.toLowerCase)

    val requiredColumns = tableSchema.filter {
      field => requiredFieldNames.contains(
        field.name.toLowerCase
      )
    }

    scanSchema = StructType(requiredColumns)
    scanConfig = scanConfig.withOutFields(requiredColumns.map(_.name))
  }

  /**
   * Builds a scan for the configured table schema and read options.
   *
   * @return an [[ArcgisScan]]
   * @since 0.1.0
   */
  override def build(): Scan = {

    new ArcgisScan(
      scanConfig,
      scanSchema
    )
  }
}
