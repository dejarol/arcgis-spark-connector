package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.read.partitioning.ArcgisPartition
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.{InputPartition, PartitionReader, PartitionReaderFactory}

class ArcgisPartitionReaderFactory()
  extends PartitionReaderFactory {

  override def createReader(inputPartition: InputPartition): PartitionReader[InternalRow] = {

    inputPartition match {
      case ap: ArcgisPartition => new ArcgisPartitionReader(ap)
      case _ => throw new IllegalArgumentException(
        s"Unsupported input partition type: ${inputPartition.getClass}"
      )
    }
  }
}
