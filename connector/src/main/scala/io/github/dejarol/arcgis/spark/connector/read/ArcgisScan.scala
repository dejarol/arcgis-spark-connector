package io.github.dejarol.arcgis.spark.connector.read

import org.apache.spark.sql.connector.read.{Batch, Scan}
import org.apache.spark.sql.types.StructType

class ArcgisScan()
  extends Scan {

  override def readSchema(): StructType = {

    StructType(
      Array.empty
    )
  }

  override def toBatch: Batch = {

    new ArcgisBatch()
  }
}
