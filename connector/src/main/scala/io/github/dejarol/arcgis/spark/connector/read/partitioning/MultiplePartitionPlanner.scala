package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig

/**
 * TODO
 * @param featuresCount
 * @param numPartitions
 * @param fetchSize
 * @param queryLayerConfig
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
   * TODO
   * @param resultOffset
   * @return
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