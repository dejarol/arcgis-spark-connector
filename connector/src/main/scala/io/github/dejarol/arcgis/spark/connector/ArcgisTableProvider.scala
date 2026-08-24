package io.github.dejarol.arcgis.spark.connector

import org.apache.spark.sql.connector.catalog.{Table, TableProvider}
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.sources.DataSourceRegister
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.util

/**
 * TODO
 */
class ArcgisTableProvider
  extends TableProvider
    with DataSourceRegister {

  import ArcgisTableProvider._

  override def inferSchema(caseInsensitiveStringMap: CaseInsensitiveStringMap): StructType = {
    null
  }

  override def getTable(structType: StructType, transforms: Array[Transform], map: util.Map[String, String]): Table = {

    new ArcgisTable(
      structType, map
    )
  }

  override def shortName(): String = SHORT_NAME
}

object ArcgisTableProvider {

  final val SHORT_NAME = "arcgis"
}
