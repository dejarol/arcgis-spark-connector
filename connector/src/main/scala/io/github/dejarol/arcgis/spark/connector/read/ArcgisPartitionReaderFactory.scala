package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
import io.github.dejarol.arcgis.spark.connector.read.encoding.ArcgisFeatureToInternalRowEncoderImpl
import io.github.dejarol.arcgis.spark.connector.read.partitioning.ArcgisPartition
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.{InputPartition, PartitionReader, PartitionReaderFactory}

class ArcgisPartitionReaderFactory(private val readConfig: ReadConfig)
  extends PartitionReaderFactory {

  override def createReader(inputPartition: InputPartition): PartitionReader[InternalRow] = {

    inputPartition match {
      case partition: ArcgisPartition => setupPartitionReader(partition)
      case _ => throw new IllegalArgumentException(
        s"Unsupported input partition type: ${inputPartition.getClass}"
      )
    }
  }

  private def setupPartitionReader(partition: ArcgisPartition): PartitionReader[InternalRow] = {

    val (layerFields, layerGeometry) = readConfig.getFeatureLayerFieldsAndGeometry

    new ArcgisPartitionReader(
      readConfig,
      partition,
      new ArcgisFeatureToInternalRowEncoderImpl(
        layerFields, layerGeometry, readConfig.shouldReturnGeometry
      )
    )
  }
}
