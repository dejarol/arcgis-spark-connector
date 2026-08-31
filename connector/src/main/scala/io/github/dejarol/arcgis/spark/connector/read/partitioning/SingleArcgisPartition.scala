package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig

/**
 * TODO
 * @param featuresCount
 * @param fetchSize
 * @param queryLayerConfig
 */
case class SingleArcgisPartition(
                                  private val featuresCount: Int,
                                  private val fetchSize: Int,
                                  private val queryLayerConfig: QueryLayerConfig
                                )
  extends ArcgisPartition {

  override def partitionId: Int = 0

  override def parametersForPartitionQueries: Seq[QueryLayerParameters] = {

    // [1] If there are less features than the fetch size
    if (featuresCount <= fetchSize) {
      // [1.1] Return a single query that does not set result offset and record count
      Seq(queryLayerConfig.asQueryParameters)
    }

    else {

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
}
