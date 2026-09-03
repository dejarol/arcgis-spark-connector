package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}
import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

/**
 * Query options sent to an ArcGIS feature layer query endpoint.
 *
 * @param properties configuration entries keyed by property name
 * @since 0.1.0
 */
case class QueryLayerConfig(override protected val properties: CaseInsensitiveMap[String])
  extends BaseConfig(properties) {

  import QueryLayerConfig._

  /**
   * Returns the ArcGIS `where` clause, if set.
   *
   * @return the where expression, or `None` if unset
   * @since 0.1.0
   */
  def where: Option[String] = get(WHERE_KEY)

  /**
   * TODO
   * @return
   */
  def objectIDs: Option[Seq[Int]] = getAs[Seq[Int]](OBJECT_IDS_KEY, PropertyConversions.toListOfInt)

  /**
   * Returns the output field names, if set.
   *
   * @return the field names, or `None` if unset
   * @since 0.1.0
   */
  def outFields: Option[Seq[String]] = getAs[Seq[String]](OUT_FIELDS_KEY, PropertyConversions.toListOfString)

  /**
   * Returns whether the query should include geometry, if set.
   *
   * @return `true` or `false` when the option is present, otherwise `None`
   * @since 0.1.0
   */
  def returnGeometry: Option[Boolean] = getAs[Boolean](RETURN_GEOMETRY_KEY, PropertyConversions.ToBoolean)

  /**
   * Returns the output spatial reference WKID, if set.
   *
   * @return the spatial reference identifier, or `None` if unset
   * @since 0.1.0
   */
  def outSR: Option[Int] = getAs[Int](OUT_SR_KEY, PropertyConversions.ToInteger)

  /**
   * Builds the query parameters represented by this configuration.
   *
   * @return query parameters ready to be sent to ArcGIS
   * @since 0.1.0
   */
  def asQueryParameters: QueryLayerParameters = {

    QueryLayerParameters(
      where = where,
      objectIDs = objectIDs,
      outFields = outFields,
      returnGeometry = returnGeometry,
      outSR = outSR
    )
  }
}

object QueryLayerConfig {

  /**
   * Property key for the ArcGIS `where` clause.
   *
   * @since 0.1.0
   */
  final val WHERE_KEY = "where"

  /**
   * TODO
   */
  final val OBJECT_IDS_KEY = "objectIds"

  /**
   * Property key for the comma-separated output field names.
   *
   * @since 0.1.0
   */
  final val OUT_FIELDS_KEY = "outFields"

  /**
   * Property key for whether the query should include geometry.
   *
   * @since 0.1.0
   */
  final val RETURN_GEOMETRY_KEY = "returnGeometry"

  /**
   * Property key for the output spatial reference WKID.
   *
   * @since 0.1.0
   */
  final val OUT_SR_KEY = "outSR"
}
