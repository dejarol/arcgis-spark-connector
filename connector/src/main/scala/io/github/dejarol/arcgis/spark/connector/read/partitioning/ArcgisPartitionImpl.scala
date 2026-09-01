package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters

/**
 * TODO
 * @param id
 * @param parameters
 */
case class ArcgisPartitionImpl(
                                private val id: Int,
                                private val parameters: Seq[QueryLayerParameters]
                                )
  extends ArcgisPartition {

  override def partitionId: Int = id

  override def parametersForPartitionQueries: Seq[QueryLayerParameters] = parameters
}
