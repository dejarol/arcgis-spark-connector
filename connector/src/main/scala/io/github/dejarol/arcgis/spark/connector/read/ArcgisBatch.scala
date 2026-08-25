package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReaderFactory}

class ArcgisBatch(private val readConfig: ReadConfig)
  extends Batch {

  override def planInputPartitions(): Array[InputPartition] = {

    Array.empty
  }

  override def createReaderFactory(): PartitionReaderFactory = {

    new ArcgisPartitionReaderFactory()
  }
}
