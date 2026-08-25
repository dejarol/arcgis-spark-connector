package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import io.github.dejarol.arcgis.spark.connector.read.partitioning.{ArcgisPartition, SingleArcgisPartition}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReaderFactory}

class ArcgisBatch(private val readConfig: ReadConfig)
  extends Batch
    with Logging {

  override def planInputPartitions(): Array[InputPartition] = {

    val featuresCount = readConfig.returnCountOnly
    val partitions: Seq[InputPartition] = if (!readConfig.partitioningIsConfigured) {
      planSinglePartition(featuresCount)
    } else {
      planMultiplePartitions()
    }

    partitions.toArray
  }

  /**
   * TODO
   * @param featuresCount
   * @return
   */
  private def planSinglePartition(featuresCount: Int): Seq[ArcgisPartition] = {

    Seq(
      SingleArcgisPartition(
        featuresCount,
        readConfig.partitioningConfig.maxRecordsPerQuery,
        readConfig.queryLayerConfig
      )
    )
  }

  private def planMultiplePartitions(): Seq[ArcgisPartition] = {

   Seq.empty
  }

  override def createReaderFactory(): PartitionReaderFactory = {

    new ArcgisPartitionReaderFactory()
  }
}
