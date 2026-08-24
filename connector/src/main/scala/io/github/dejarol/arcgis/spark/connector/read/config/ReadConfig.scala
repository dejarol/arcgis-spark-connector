package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{ParentConfig, PropertyConversions}
import sttp.model.Uri

import java.util

/**
 * TODO
 * @param properties
 */
case class ReadConfig(override protected val properties: util.Map[String, String])
  extends ParentConfig(properties) {

  import ReadConfig._

  /**
   * TODO
   * @return
   */
  def layerUri: Option[Uri] = getAs[Uri](LAYER_URI_KEY, PropertyConversions.ToUri)
}

object ReadConfig {

  final val LAYER_URI_KEY = "layerUri"
}
