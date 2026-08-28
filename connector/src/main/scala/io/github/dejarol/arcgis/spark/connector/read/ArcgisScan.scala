package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Batch, Scan}
import org.apache.spark.sql.types.{StructField, StructType}

class ArcgisScan(
                  private val readConfig: ReadConfig,
                  private val tableSchema: StructType
                )
  extends Scan {

  override def readSchema(): StructType = tableSchema

  override def toBatch: Batch = {

    new ArcgisBatch(readConfig)
  }
}
