package io.github.dejarol.arcgis.spark.connector.read

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.read.PartitionReader

class ArcgisPartitionReader(private val partition: ArcgisPartition)
  extends PartitionReader[InternalRow] {

  override def next(): Boolean = {
    false
  }

  override def get(): InternalRow = {
    InternalRow()
  }

  override def close(): Unit = {

  }
}
