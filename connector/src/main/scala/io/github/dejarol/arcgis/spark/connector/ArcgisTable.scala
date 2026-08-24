package io.github.dejarol.arcgis.spark.connector

import org.apache.spark.sql.connector.catalog.{SupportsRead, Table, TableCapability}
import org.apache.spark.sql.connector.read.ScanBuilder
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.util

class ArcgisTable(
                   private val tableSchema: StructType,
                   private val tableProperties: util.Map[String, String]
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

    val overallMap = caseInsensitiveStringMap.asCaseSensitiveMap()
    overallMap.putAll(tableProperties)
    new CaseInsensitiveStringMap(overallMap)

    null
  }
}
