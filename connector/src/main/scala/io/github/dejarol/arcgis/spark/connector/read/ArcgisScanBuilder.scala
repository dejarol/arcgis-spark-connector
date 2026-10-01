package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.schema.SchemaUtils
import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Scan, ScanBuilder, SupportsPushDownRequiredColumns}
import org.apache.spark.sql.types.{StructField, StructType}

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

  /**
   * Restricts the scan to the columns required by Spark.
   *
   * Updates the scan schema and forwards the required attribute names and geometry
   * flag to the read configuration.
   *
   * @param requiredSchema schema of the columns requested by the query
   * @since 0.2.0
   */
  override def pruneColumns(requiredSchema: StructType): Unit = {

    val requiredFieldNames = requiredSchema.fieldNames.toSet[String].map(_.toLowerCase)

    val requiredColumns = tableSchema.filter {
      field => requiredFieldNames.contains(
        field.name.toLowerCase
      )
    }

    scanSchema = StructType(requiredColumns)
    scanConfig = updateScanConfigForRequiredColumns(requiredColumns)
  }

  /**
   * Copies the scan configuration so that it requests only the required columns.
   *
   * @param requiredColumns fields retained after column pruning
   * @return a configuration with `outFields` and `returnGeometry` set from `requiredColumns`
   * @since 0.2.0
   */
  private def updateScanConfigForRequiredColumns(
                                                  requiredColumns: Seq[StructField],
                                                ): ReadConfig = {

    // Columns to set as 'outFields': geometry column should not be included
    val attributeColumnsNames = requiredColumns.collect {
      case sf if !sf.name.equals(SchemaUtils.GEOMETRY_COLUMN_NAME) =>
        sf.name
    }

    // If the geometry column is not included in the required columns,
    // even tough the flag 'returnGeometry' was set to true, we disable it
    val requiredColumnsIncludeGeometryColumn = requiredColumns.exists {
      _.name.equalsIgnoreCase(SchemaUtils.GEOMETRY_COLUMN_NAME)
    }

    // Update the scan config
    scanConfig
      .withOutFields(attributeColumnsNames)
      .withReturnGeometry(requiredColumnsIncludeGeometryColumn)
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
