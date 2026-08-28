package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Scan, ScanBuilder}
import org.apache.spark.sql.types.StructType

class ArcgisScanBuilder(
                         private val readConfig: ReadConfig,
                         private val tableSchema: StructType
                       )
  extends ScanBuilder {

  override def build(): Scan = {

    new ArcgisScan(readConfig, tableSchema)
  }
}
