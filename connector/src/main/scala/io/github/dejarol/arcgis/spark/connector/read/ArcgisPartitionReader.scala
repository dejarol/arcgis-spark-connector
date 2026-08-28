package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import io.github.dejarol.arcgis.spark.connector.read.encoding.ArcgisFeatureToInternalRowEncoder
import io.github.dejarol.arcgis.spark.connector.read.models.{ArcgisFeature, QueryResponse}
import io.github.dejarol.arcgis.spark.connector.read.partitioning.ArcgisPartition
import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.PartitionReader

/**
 * TODO
 * @param readConfig
 * @param partition
 * @param mapper
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
    case r: QueryResponse if r.nonEmpty => r.features
  }.flatten.toIterator

  override def next(): Boolean = featureIterator.hasNext

  override def get(): InternalRow = mapper(featureIterator.next())

  override def close(): Unit = {

    log.info(f"Closing reader for partition ${partition.partitionId}")
  }
}
