package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig

/**
 * Plans multiple [[ArcgisPartition]] instances by splitting the feature count across partitions.
 *
 * @param featuresCount    number of features in the layer
 * @param numPartitions    number of partitions to create
 * @param fetchSize        maximum number of features fetched per query
 * @param queryLayerConfig query options used as the base for each partition query
 * @since 0.1.0
 */
case class MultiplePartitionPlanner(
                                     private val featuresCount: Int,
                                     private val numPartitions: Int,
                                     private val fetchSize: Int,
                                     private val queryLayerConfig: QueryLayerConfig
                                   )
  extends ArcgisPartitionPlanner {

  private lazy val resultRecordCountForPartition = (
    featuresCount.toDouble / numPartitions.toDouble
    ).ceil.toInt

  /**
   * Plans partitions that together cover all features in the layer.
   *
   * @return one [[ArcgisPartition]] per planned slice of the feature count
   * @since 0.1.0
   */
  override def plan(): Seq[ArcgisPartition] = {

    Range.inclusive(
      0, featuresCount, resultRecordCountForPartition
    ).zipWithIndex.map {
      case (resultOffset, partitionId) =>
        ArcgisPartitionImpl(
          partitionId,
          setQueriesForPartition(resultOffset)
        )
    }
  }

  /**
   * Builds the paged queries that fetch features for one partition.
   *
   * @param resultOffset first feature offset assigned to the partition
   * @return one query parameter set per page in the partition
   * @since 0.1.0
   */
  private def setQueriesForPartition(
                                      resultOffset: Int
                                    ): Seq[QueryLayerParameters] = {

    // [1] Calculate the end offset for the partition
    val rangeEndOffset = Math.min(
      resultOffset + resultRecordCountForPartition,
      featuresCount
    )

    // [2] Generate queries for the partition
    Range.inclusive(
      resultOffset, rangeEndOffset, fetchSize
    ).map {
      offset =>

        // [2.1] Calculate the number of records to fetch
        val resultRecordCount = Math.min(
          fetchSize, rangeEndOffset - offset
        )

        queryLayerConfig.asQueryParameters
          .withResultOffsetAndRecordCount(offset, resultRecordCount)
    }
  }
}