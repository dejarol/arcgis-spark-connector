package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import org.apache.spark.sql.connector.read.InputPartition

/**
 * Concrete implementation of Spark's connector [[org.apache.spark.sql.connector.read.InputPartition]] for ARCGIS datasource
 *
 * @since 0.1.0
 */
trait ArcgisPartition
  extends InputPartition {

  /**
   * Returns the identifier of this partition.
   *
   * @return the partition id
   * @since 0.1.0
   */
  def partitionId: Int

  /**
   * Returns the query parameters used to fetch this partition's features.
   *
   * @return one query parameter set per request required to read the partition
   * @since 0.1.0
   */
  def parametersForPartitionQueries: Seq[QueryLayerParameters]
}
