package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import io.github.dejarol.arcgis.spark.connector.read.partitioning.{ArcgisPartition, SingleArcgisPartition}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReaderFactory}

/**
 * Concrete implementation of Spark's connector [[Batch]] for ARCGIS datasource
 *
 * @param readConfig options that control how the feature layer is queried
 * @since 0.1.0
 */
class ArcgisBatch(private val readConfig: ReadConfig)
  extends Batch
    with Logging {

  /**
   * Plans the input partitions for this batch scan.
   *
   * @return one partition when partitioning is unset; otherwise multiple partitions
   * @since 0.1.0
   */
  override def planInputPartitions(): Array[InputPartition] = {

    val featuresCount = readConfig.returnCountOnly
    val partitions: Seq[InputPartition] = if (readConfig.partitioningIsConfigured) {
      planMultiplePartitions()
    } else {
      planSinglePartition(featuresCount)
    }

    partitions.toArray
  }

  /**
   * Plans a single partition covering the full feature count.
   *
   * @param featuresCount number of features in the layer
   * @return a sequence containing one [[SingleArcgisPartition]]
   * @since 0.1.0
   */
  private def planSinglePartition(featuresCount: Int): Seq[ArcgisPartition] = {

    Seq(
      SingleArcgisPartition(
        featuresCount,
        readConfig.partitioningConfig.fetchSize,
        readConfig.queryLayerConfig
      )
    )
  }

  /**
   * Plans multiple partitions when partitioning options are configured.
   *
   * @return the planned ArcGIS partitions
   * @throws UnsupportedOperationException always; multi-partition planning is not implemented
   * @since 0.1.0
   */
  private def planMultiplePartitions(): Seq[ArcgisPartition] = {

   throw new UnsupportedOperationException("TODO")
  }

  /**
   * Creates a factory of partition readers for this batch.
   *
   * @return an [[ArcgisPartitionReaderFactory]] using this batch's read options
   * @since 0.1.0
   */
  override def createReaderFactory(): PartitionReaderFactory = {

    new ArcgisPartitionReaderFactory(readConfig)
  }
}
