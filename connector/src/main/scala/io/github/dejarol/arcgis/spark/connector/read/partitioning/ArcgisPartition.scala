package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.FeatureLayerQueryParameters
import org.apache.spark.sql.connector.read.InputPartition

/**
 * TODO
 */
trait ArcgisPartition
  extends InputPartition {

  /**
   * TODO
   * @return
   */
  def partitionId: Int

  /**
   * TODO
   * @return
   */
  def parametersForPartitionQueries: Seq[FeatureLayerQueryParameters]
}
