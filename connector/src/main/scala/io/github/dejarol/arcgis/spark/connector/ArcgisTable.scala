package io.github.dejarol.arcgis.spark.connector

import io.github.dejarol.arcgis.spark.connector.core.JavaCollectionsUtils
import io.github.dejarol.arcgis.spark.connector.read.ArcgisScanBuilder
import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.catalog.{SupportsRead, Table, TableCapability}
import org.apache.spark.sql.connector.read.ScanBuilder
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.util

/**
 * Concrete implementation of Spark's connector [[Table]] for ARCGIS datasource
 *
 * @param tableSchema     Spark schema of the feature layer
 * @param tableProperties table options used to configure reads
 * @since 0.1.0
 */
class ArcgisTable(
                   private val tableSchema: StructType,
                   private val tableProperties: CaseInsensitiveStringMap
                 )
  extends Table
    with SupportsRead {

  /**
   * Returns the name of this table.
   *
   * @return the table name
   * @since 0.1.0
   */
  override def name(): String = "todo"

  /**
   * Returns the Spark schema of this table.
   *
   * @return the table schema
   * @since 0.1.0
   */
  override def schema(): StructType = tableSchema

  /**
   * Returns the capabilities supported by this table.
   *
   * @return a set containing [[TableCapability.BATCH_READ]]
   * @since 0.1.0
   */
  override def capabilities(): util.Set[TableCapability] = {

    new util.HashSet[TableCapability]() {{
      add(TableCapability.BATCH_READ)
    }}
  }

  /**
   * Creates a scan builder from this table's schema and merged read options.
   *
   * @param caseInsensitiveStringMap scan options to merge with the table properties
   * @return a scan builder for this table
   * @since 0.1.0
   */
  override def newScanBuilder(caseInsensitiveStringMap: CaseInsensitiveStringMap): ScanBuilder = {

    val readConfig = ReadConfig(
      JavaCollectionsUtils.mergeCaseInsensitiveMaps(
        tableProperties, caseInsensitiveStringMap
      )
    )

    new ArcgisScanBuilder(readConfig, schema())
  }
}
