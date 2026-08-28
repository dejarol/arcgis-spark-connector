package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import io.github.dejarol.arcgis.spark.connector.read.encoding.ArcgisFeatureToInternalRowEncoder
import io.github.dejarol.arcgis.spark.connector.read.models.{ArcgisFeature, QueryLayerResponse}
import io.github.dejarol.arcgis.spark.connector.read.partitioning.ArcgisPartition
import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.PartitionReader

/**
 * Concrete implementation of Spark's connector [[PartitionReader]] for ARCGIS datasource
 *
 * @param readConfig options that control how the feature layer is queried
 * @param partition  partition whose features are read
 * @param mapper     encoder from ArcGIS features to InternalRows
 * @since 0.1.0
 */
class ArcgisPartitionReader(
                           private val readConfig: ReadConfig,
                           private val partition: ArcgisPartition,
                           private val mapper: ArcgisFeatureToInternalRowEncoder
                           )
  extends PartitionReader[InternalRow]
    with Logging {

  private lazy val featureIterator: Iterator[ArcgisFeature] = partition.parametersForPartitionQueries.map {
    parameters => readConfig.queryUsingPost(parameters)
  }.collect {
    case r: QueryLayerResponse if r.nonEmpty => r.features
  }.flatten.toIterator

  /**
   * Reports whether another row is available in this partition.
   *
   * @return `true` if a next feature remains, `false` otherwise
   * @since 0.1.0
   */
  override def next(): Boolean = featureIterator.hasNext

  /**
   * Returns the next feature encoded as an InternalRow.
   *
   * @return the encoded row
   * @since 0.1.0
   */
  override def get(): InternalRow = mapper(featureIterator.next())

  /**
   * Closes this partition reader.
   *
   * @since 0.1.0
   */
  override def close(): Unit = {

    log.info(f"Closing reader for partition ${partition.partitionId}")
  }
}
