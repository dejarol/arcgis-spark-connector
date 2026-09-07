package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import io.github.dejarol.arcgis.spark.connector.read.partitioning.{ArcgisPartition, MultiplePartitionPlanner, SinglePartitionPlanner}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReaderFactory}

/**
 * Concrete implementation of Spark's connector [[org.apache.spark.sql.connector.read.Batch]]
 * for ARCGIS datasource
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
   * @throws java.lang.IllegalArgumentException if the configured partition count is zero or negative
   * @since 0.1.0
   */
  override def planInputPartitions(): Array[InputPartition] = {

    val featuresCount = readConfig.returnCountOnly
    val numPartitions: Option[Int] = readConfig.partitioningConfig.numPartitions
    val partitions = numPartitions match {
      case Some(1) | None => planSinglePartition(featuresCount)
      case Some(n) if n > 1 => planMultiplePartitions(featuresCount, n)
      case Some(n) if n <= 0 => throw new IllegalArgumentException(
        s"Invalid number of partitions: $n. Should be greater than 1"
      )
    }

    log.info(f"Planned ${partitions.size} partition(s)")
    partitions.toArray
  }

  /**
   * Plans a single partition covering the full feature count.
   *
   * @param featuresCount number of features in the layer
   * @return a sequence containing a single [[ArcgisPartition]]
   * @since 0.1.0
   */
  private def planSinglePartition(featuresCount: Int): Seq[ArcgisPartition] = {

    SinglePartitionPlanner(
      featuresCount,
      readConfig.partitioningConfig.fetchSize,
      readConfig.queryLayerConfig
    ).plan()
  }

  /**
   * Plans multiple partitions when partitioning options are configured.
   *
   * @param featuresCount number of features in the layer
   * @param partitions    number of partitions to create
   * @return the planned ArcGIS partitions
   * @since 0.1.0
   */
  private def planMultiplePartitions(
                                      featuresCount: Int,
                                      partitions: Int
                                    ): Seq[ArcgisPartition] = {

    MultiplePartitionPlanner(
      featuresCount,
      partitions,
      readConfig.partitioningConfig.fetchSize,
      readConfig.queryLayerConfig
    ).plan()
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
