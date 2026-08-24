package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.EsriGeometryType
import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}
import io.github.dejarol.arcgis.spark.connector.core.http.item.FeatureLayerField
import io.github.dejarol.arcgis.spark.connector.read.http.ReadRequestHandler
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
  def layerUri: Uri = unsafelyGetAs[Uri](LAYER_URI_KEY, PropertyConversions.ToUri)

  /**
   * TODO
   * @return
   */
  def queryLayerConfig: QueryLayerConfig = {

    QueryLayerConfig(
      propertiesStartingWithPrefix(QUERY_PREFIX)
    )
  }

  /**
   * TODO
   * @return
   */
  def shouldReturnGeometry: Boolean = queryLayerConfig.returnGeometry.getOrElse(false)

  /**
   * @param action
   * @tparam R
   * @return
   */
  private def withRequestHandlerDo[R](action: ReadRequestHandler => R): R = {

    action(
      ReadRequestHandler.withDefaultBackend()
    )
  }

  def getFeatureLayerFieldsAndGeometry: (Seq[FeatureLayerField], EsriGeometryType) = {

    withRequestHandlerDo {
      _.getFeatureLayerFieldsAndGeometry(
        layerUri, queryLayerConfig.outFields, None
      )
    }
  }
}

object ReadConfig {

  final val LAYER_URI_KEY = "layerUri"
  final val QUERY_PREFIX = "query."
}
