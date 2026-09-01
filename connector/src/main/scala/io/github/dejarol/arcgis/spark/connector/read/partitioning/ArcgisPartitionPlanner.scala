package io.github.dejarol.arcgis.spark.connector.read.partitioning

/**
 * TODO
 */
trait ArcgisPartitionPlanner {

  /**
   * TODO
   * @return
   */
  def plan(): Seq[ArcgisPartition]
}
