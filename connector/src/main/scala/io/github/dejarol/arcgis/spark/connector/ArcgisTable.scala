package io.github.dejarol.arcgis.spark.connector

import io.github.dejarol.arcgis.spark.connector.core.JavaCollectionsUtils
import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.catalog.{SupportsRead, Table, TableCapability}
import org.apache.spark.sql.connector.read.ScanBuilder
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.util

/**
 * TODO
 * @param tableSchema
 * @param tableProperties
 */
class ArcgisTable(
                   private val tableSchema: StructType,
                   private val tableProperties: CaseInsensitiveStringMap
                 )
  extends Table
    with SupportsRead {

  override def name(): String = "todo"

  override def schema(): StructType = tableSchema

  override def capabilities(): util.Set[TableCapability] = {

    new util.HashSet[TableCapability]() {{
      add(TableCapability.BATCH_READ)
    }}
  }

  override def newScanBuilder(caseInsensitiveStringMap: CaseInsensitiveStringMap): ScanBuilder = {

    val readConfig = ReadConfig(
      JavaCollectionsUtils.mergeCaseInsensitiveMaps(
        tableProperties, caseInsensitiveStringMap
      )
    )
    null
  }
}
