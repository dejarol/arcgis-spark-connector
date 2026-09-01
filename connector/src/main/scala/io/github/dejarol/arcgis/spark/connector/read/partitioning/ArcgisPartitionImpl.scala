package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters

/**
 * Default [[ArcgisPartition]] that stores a partition identifier and its query pages.
 *
 * @param id         identifier of this partition
 * @param parameters query parameter sets used to fetch this partition's features
 * @since 0.1.0
 */
case class ArcgisPartitionImpl(
                                private val id: Int,
                                private val parameters: Seq[QueryLayerParameters]
                                )
  extends ArcgisPartition {

  /**
   * Returns the identifier of this partition.
   *
   * @return the partition id
   * @since 0.1.0
   */
  override def partitionId: Int = id

  /**
   * Returns the query parameters used to fetch this partition's features.
   *
   * @return one query parameter set per request required to read the partition
   * @since 0.1.0
   */
  override def parametersForPartitionQueries: Seq[QueryLayerParameters] = parameters
}
