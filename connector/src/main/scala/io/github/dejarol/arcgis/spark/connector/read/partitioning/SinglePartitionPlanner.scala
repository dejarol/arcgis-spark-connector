package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig

/**
 * Plans a single [[ArcgisPartition]] that covers the entire feature layer.
 *
 * @param featuresCount    number of features in the layer
 * @param fetchSize        maximum number of features fetched per query
 * @param queryLayerConfig query options used as the base for each partition query
 * @since 0.1.0
 */
case class SinglePartitionPlanner(
                                   private val featuresCount: Int,
                                   private val fetchSize: Int,
                                   private val queryLayerConfig: QueryLayerConfig
                                 )
  extends ArcgisPartitionPlanner {

  /**
   * Plans a single partition covering the full feature count.
   *
   * @return a sequence containing one [[ArcgisPartition]]
   * @since 0.1.0
   */
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
   * Builds paged queries that cover the full feature count.
   *
   * @return one query parameter set per page
   * @since 0.1.0
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
