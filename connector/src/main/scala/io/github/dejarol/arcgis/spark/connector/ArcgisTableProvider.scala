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
 * Concrete implementation of Spark's connector [[org.apache.spark.sql.connector.catalog.TableProvider]]
 * for ARCGIS datasource
 *
 * @since 0.1.0
 */
class ArcgisTableProvider
  extends TableProvider
    with DataSourceRegister {

  import ArcgisTableProvider._

  /**
   * Infers the Spark schema of the ArcGIS feature layer from the read options.
   *
   * @param caseInsensitiveStringMap data source options used to load the layer
   * @return a Spark schema for the layer's fields and, when requested, geometry
   * @since 0.1.0
   */
  override def inferSchema(caseInsensitiveStringMap: CaseInsensitiveStringMap): StructType = {

    // [1.1] Extract feature layer fields and geometry
    val readConfig = ReadConfig.fromCIMap(caseInsensitiveStringMap)
    val (featureLayerFields, geometry) = readConfig.getFeatureLayerFieldsAndGeometry
    val maybeGeometry = if (readConfig.shouldReturnGeometry) {
      Some(geometry)
    } else None

    // [1.2] Invoke schema conversion utils
    SchemaUtils.toStructType(
      featureLayerFields, maybeGeometry
    )
  }

  /**
   * Creates an ArcGIS table from a schema and data source options.
   *
   * @param structType Spark schema of the table
   * @param transforms partitioning transforms requested by Spark
   * @param map        data source options
   * @return an [[ArcgisTable]] for the given schema and options
   * @since 0.1.0
   */
  override def getTable(structType: StructType, transforms: Array[Transform], map: util.Map[String, String]): Table = {

    new ArcgisTable(
      structType, new CaseInsensitiveStringMap(map)
    )
  }

  /**
   * Returns the short name used to register this data source.
   *
   * @return [[ArcgisTableProvider.SHORT_NAME]]
   * @since 0.1.0
   */
  override def shortName(): String = SHORT_NAME
}

object ArcgisTableProvider {

  /**
   * Short name used to register this data source with Spark.
   *
   * @since 0.1.0
   */
  final val SHORT_NAME = "arcgis"
}
