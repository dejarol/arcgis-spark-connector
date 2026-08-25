package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Scan, ScanBuilder}

class ArcgisScanBuilder(private val readConfig: ReadConfig)
  extends ScanBuilder {

  override def build(): Scan = {

    new ArcgisScan(readConfig)
  }
}
