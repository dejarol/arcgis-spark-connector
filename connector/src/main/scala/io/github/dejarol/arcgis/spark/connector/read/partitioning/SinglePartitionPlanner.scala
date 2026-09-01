package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig

/**
 * TODO
 * @param featuresCount
 * @param fetchSize
 * @param queryLayerConfig
 */
case class SinglePartitionPlanner(
                                   private val featuresCount: Int,
                                   private val fetchSize: Int,
                                   private val queryLayerConfig: QueryLayerConfig
                                 )
  extends ArcgisPartitionPlanner {

  override def plan(): Seq[ArcgisPartition] = {

    // [1] If there are fewer features than the fetch size
    val queryParameters: Seq[QueryLayerParameters] = if (featuresCount <= fetchSize) {
      // [1.1] Return a single query that does not set result offset and record count
      Seq(queryLayerConfig.asQueryParameters)
    } else planQueries()

    // [3] Return the sequence of queries in a single partition
    Seq(
      ArcgisPartitionImpl(0, queryParameters)
    )
  }

  /**
   * TODO
   * @return
   */
  private def planQueries(): Seq[QueryLayerParameters] = {

    // [2] Otherwise, return a sequence of queries
    Range.inclusive(
      0, featuresCount, fetchSize
    ).map {
      offset =>

        val resultRecordCount = Math.min(fetchSize, featuresCount - offset)
        queryLayerConfig
          .asQueryParameters
          .withResultOffsetAndRecordCount(offset, resultRecordCount)
    }
  }
}
