package io.github.dejarol.arcgis.spark.connector

import io.github.dejarol.arcgis.spark.connector.core.schema.SchemaUtils
import io.github.dejarol.arcgis.spark.connector.read.config.ReadConfig
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

    // [1.1] Extract feature layer fields and geometry
    val readConfig = ReadConfig(caseInsensitiveStringMap)
    val (featureLayerFields, geometry) = readConfig.getFeatureLayerFieldsAndGeometry
    val maybeGeometry = if (readConfig.shouldReturnGeometry) {
      Some(geometry)
    } else None

    // [1.2] Invoke schema conversion utils
    SchemaUtils.toStructType(
      featureLayerFields, maybeGeometry
    )
  }

  override def getTable(structType: StructType, transforms: Array[Transform], map: util.Map[String, String]): Table = {

    new ArcgisTable(
      structType, new CaseInsensitiveStringMap(map)
    )
  }

  override def shortName(): String = SHORT_NAME
}

object ArcgisTableProvider {

  final val SHORT_NAME = "arcgis"
}
