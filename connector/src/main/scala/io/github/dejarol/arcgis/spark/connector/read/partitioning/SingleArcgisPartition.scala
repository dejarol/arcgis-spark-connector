package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig

/**
 * TODO
 * @param featuresCount
 * @param maxRecordsPerQuery
 * @param queryLayerConfig
 */
case class SingleArcgisPartition(
                                  private val featuresCount: Int,
                                  private val maxRecordsPerQuery: Int,
                                  private val queryLayerConfig: QueryLayerConfig
                                )
  extends ArcgisPartition {

  override def partitionId: Int = 0

  override def parametersForPartitionQueries: Seq[QueryLayerParameters] = {

    Range.inclusive(
      0, featuresCount, maxRecordsPerQuery
    ).map {
      offset =>

        val resultRecordCount = Math.min(
          maxRecordsPerQuery, featuresCount - offset
        )

        queryLayerConfig
          .asQueryParameters
          .withResultOffsetAndRecordCount(offset, resultRecordCount)
    }
  }
}
