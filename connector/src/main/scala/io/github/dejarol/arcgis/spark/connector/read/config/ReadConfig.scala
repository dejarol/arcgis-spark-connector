package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureLayerField}
import io.github.dejarol.arcgis.spark.connector.read.FeatureLayerQueryParameters
import io.github.dejarol.arcgis.spark.connector.read.http.ReadRequestHandler
import io.github.dejarol.arcgis.spark.connector.read.models.QueryResponse
import sttp.model.Uri

import java.util

/**
 * Spark DataSource read options for querying an ArcGIS feature layer.
 *
 * @param properties configuration entries keyed by property name
 * @since 0.1.0
 */
case class ReadConfig(override protected val properties: util.Map[String, String])
  extends BaseConfig(properties) {

  import ReadConfig._

  /**
   * Returns the URI of the ArcGIS feature layer to read.
   *
   * @return the parsed layer URI
   * @since 0.1.0
   */
  def layerUri: Uri = unsafelyGetAs[Uri](LAYER_URI_KEY, PropertyConversions.ToUri)

  /**
   * Returns query options taken from properties prefixed with [[QUERY_PREFIX]].
   *
   * @return a query configuration with the prefix stripped from each key
   * @since 0.1.0
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
  def partitioningConfig: PartitioningConfig = {

    PartitioningConfig(
      propertiesStartingWithPrefix(PARTITIONING_PREFIX)
    )
  }

  /**
   * TODO
   * @return
   */
  def partitioningIsConfigured: Boolean = partitioningConfig.nonEmpty

  /**
   * Reports whether the query should include geometry.
   *
   * @return the `returnGeometry` query option, or `false` when it is unset
   * @since 0.1.0
   */
  def shouldReturnGeometry: Boolean = queryLayerConfig.returnGeometry.getOrElse(false)

  /**
   * Runs an action against a request handler that uses the default STTP backend.
   *
   * @param action function applied to the request handler
   * @tparam R result type of `action`
   * @return the result of `action`
   * @since 0.1.0
   */
  private def withRequestHandlerDo[R](action: ReadRequestHandler => R): R = {

    action(
      ReadRequestHandler.withDefaultBackend()
    )
  }

  /**
   * Fetches the feature layer's fields and geometry type from ArcGIS.
   *
   * @return the layer fields (filtered by `outFields` when set) and geometry type
   * @since 0.1.0
   */
  def getFeatureLayerFieldsAndGeometry: (Seq[FeatureLayerField], EsriGeometryType) = {

    withRequestHandlerDo {
      _.getFeatureLayerFieldsAndGeometry(
        layerUri, queryLayerConfig.outFields, None
      )
    }
  }

  /**
   * TODO
   * @return
   */
  def returnCountOnly: Int = {

    withRequestHandlerDo {
      _.returnCountOnly(
        layerUri, queryLayerConfig.where
      ).count
    }
  }

  /**
   * TODO
   * @param queryParameters
   * @return
   */
  def queryUsingPost(queryParameters: FeatureLayerQueryParameters): QueryResponse = {

    withRequestHandlerDo {
      _.queryUsingPost(
        layerUri, queryParameters, None
      )
    }
  }
}

/**
 * Property keys used by [[ReadConfig]].
 *
 * @since 0.1.0
 */
object ReadConfig {

  /**
   * Property key for the feature layer URI.
   *
   * @since 0.1.0
   */
  final val LAYER_URI_KEY = "layerUri"

  /**
   * Prefix of properties forwarded to [[QueryLayerConfig]].
   *
   * @since 0.1.0
   */
  final val QUERY_PREFIX = "query."

  final val PARTITIONING_PREFIX = "partitioning."
}
