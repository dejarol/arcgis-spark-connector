package io.github.dejarol.arcgis.spark.connector.read.partitioning

/**
 * Plans the [[ArcgisPartition]] instances used to scan a feature layer.
 *
 * @since 0.1.0
 */
trait ArcgisPartitionPlanner {

  /**
   * Plans the partitions for a feature-layer scan.
   *
   * @return the planned ArcGIS partitions
   * @since 0.1.0
   */
  def plan(): Seq[ArcgisPartition]
}
