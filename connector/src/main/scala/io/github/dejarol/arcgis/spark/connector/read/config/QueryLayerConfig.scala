package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}
import io.github.dejarol.arcgis.spark.connector.read.QueryParameters

import java.util

/**
 * TODO
 * @param properties
 */
case class QueryLayerConfig(override protected val properties: util.Map[String, String])
  extends BaseConfig(properties) {

  import QueryLayerConfig._

  /**
   * TODO
   * @return
   */
  def where: Option[String] = get(WHERE_KEY)

  /**
   * TODO
   * @return
   */
  def outFields: Option[Seq[String]] = getAs[Seq[String]](OUT_FIELDS_KEY, PropertyConversions.ToList)

  /** TODO
   * @return
   */

  def returnGeometry: Option[Boolean] = getAs[Boolean](RETURN_GEOMETRY_KEY, PropertyConversions.ToBoolean)

  /**
   * TODo
   * @return
   */
  def outSR: Option[Int] = getAs[Int](OUT_SR_KEY, PropertyConversions.ToInteger)

  /**
   * TODO
   * @return
   */
  def asQueryParameters: QueryParameters = {

    QueryParameters(
      where,
      outFields,
      returnGeometry,
      outSR
    )
  }
}

object QueryLayerConfig {

  final val WHERE_KEY = "where"
  final val OUT_FIELDS_KEY = "outFields"
  final val RETURN_GEOMETRY_KEY = "returnGeometry"
  final val OUT_SR_KEY = "outSR"
}
