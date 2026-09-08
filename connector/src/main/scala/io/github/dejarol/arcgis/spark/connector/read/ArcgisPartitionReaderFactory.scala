package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.models.EsriGeometryType
import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import io.github.dejarol.arcgis.spark.connector.read.encoding.ArcgisFeatureToInternalRowEncoderImpl
import io.github.dejarol.arcgis.spark.connector.read.partitioning.ArcgisPartition
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.{InputPartition, PartitionReader, PartitionReaderFactory}

/**
 * Concrete implementation of Spark's connector [[org.apache.spark.sql.connector.read.PartitionReaderFactory]]
 * for ARCGIS datasource
 *
 * @param readConfig options that control how the feature layer is queried
 * @since 0.1.0
 */
class ArcgisPartitionReaderFactory(private val readConfig: ReadConfig)
  extends PartitionReaderFactory {

  /**
   * Creates a partition reader for an ArcGIS input partition.
   *
   * @param inputPartition partition to read; must be an [[ArcgisPartition]]
   * @return a reader that yields InternalRows for the partition
   * @throws java.lang.IllegalArgumentException if `inputPartition` is not an [[ArcgisPartition]]
   * @since 0.1.0
   */
  override def createReader(inputPartition: InputPartition): PartitionReader[InternalRow] = {

    inputPartition match {
      case partition: ArcgisPartition => setupPartitionReader(partition)
      case _ => throw new IllegalArgumentException(
        s"Unsupported input partition type: ${inputPartition.getClass}"
      )
    }
  }

  /**
   * Builds a partition reader with an encoder for the layer's fields and geometry.
   *
   * @param partition ArcGIS partition to read
   * @return a partition reader for `partition`
   * @since 0.1.0
   */
  private def setupPartitionReader(partition: ArcgisPartition): PartitionReader[InternalRow] = {

    val (layerFields, layerGeometry) = readConfig.getFeatureLayerFieldsAndGeometry
    val maybeGeometry: Option[EsriGeometryType] = if (readConfig.shouldReturnGeometry) {
      Some(layerGeometry)
    } else None

    new ArcgisPartitionReader(
      readConfig,
      partition,
      new ArcgisFeatureToInternalRowEncoderImpl(
        layerFields, maybeGeometry
      )
    )
  }
}
