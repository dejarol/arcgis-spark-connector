package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Batch, Scan}
import org.apache.spark.sql.types.{StructField, StructType}

class ArcgisScan(private val readConfig: ReadConfig)
  extends Scan {

  override def readSchema(): StructType = {

    StructType(
      Array.empty[StructField]
    )
  }

  override def toBatch: Batch = {

    new ArcgisBatch(readConfig)
  }
}
