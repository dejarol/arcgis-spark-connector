package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}
import sttp.model.Uri

import java.util

/**
 * TODO
 * @param properties
 */
case class ReadConfig(override protected val properties: util.Map[String, String])
  extends BaseConfig(properties) {

  import ReadConfig._

  /**
   * TODO
   * @return
   */
  def layerUri: Option[Uri] = getAs[Uri](LAYER_URI_KEY, PropertyConversions.ToUri)

  /**
   * TODO
   * @return
   */
  def queryLayerConfig: QueryLayerConfig = {

    QueryLayerConfig(
      propertiesStartingWithPrefix(QUERY_PREFIX)
    )
  }
}

object ReadConfig {

  final val LAYER_URI_KEY = "layerUri"
  final val QUERY_PREFIX = "query."
}
