package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.JavaScalaConverters
import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureLayerField}
import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.http.ReadRequestHandler
import io.github.dejarol.arcgis.spark.connector.read.models.QueryLayerResponse
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import org.apache.spark.sql.util.CaseInsensitiveStringMap
import sttp.model.Uri

/**
 * Spark DataSource read options for querying an ArcGIS feature layer.
 *
 * @param properties configuration entries keyed by property name
 * @since 0.1.0
 */
case class ReadConfig(override protected val properties: CaseInsensitiveMap[String])
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
   * Returns partitioning options taken from properties prefixed with [[PARTITIONING_PREFIX]].
   *
   * @return a partitioning configuration with the prefix stripped from each key
   * @since 0.1.0
   */
  def partitioningConfig: PartitioningConfig = {

    PartitioningConfig(
      propertiesStartingWithPrefix(PARTITIONING_PREFIX)
    )
  }

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
   * Queries the feature layer for the number of matching features.
   *
   * @return the feature count for the configured `where` clause
   * @since 0.1.0
   */
  def returnCountOnly: Int = {

    withRequestHandlerDo {
      _.returnCountOnly(
        layerUri, queryLayerConfig.where, queryLayerConfig.objectIDs, None
      ).count
    }
  }

  /**
   * Queries the feature layer using an HTTP POST.
   *
   * @param queryParameters multipart query parameters sent with the request
   * @return the query response body
   * @since 0.1.0
   */
  def queryUsingPost(queryParameters: QueryLayerParameters): QueryLayerResponse = {

    withRequestHandlerDo {
      _.queryUsingPost(
        layerUri, queryParameters, None
      )
    }
  }
}

object ReadConfig {

  /**
   * TODO
   * @param map
   * @return
   */
  def fromCIMap(map: CaseInsensitiveStringMap): ReadConfig = {

    ReadConfig(
      CaseInsensitiveMap(
        JavaScalaConverters.javaMapToScalaMap(map)
      )
    )
  }

  /**
   * TODO
   * @param first
   * @param second
   * @return
   */
  def fromUnionOf(
                   first: CaseInsensitiveStringMap,
                   second: CaseInsensitiveStringMap
                 ): ReadConfig = {

    ReadConfig(
      CaseInsensitiveMap(
        JavaScalaConverters.javaMapToScalaMap(first) ++
          JavaScalaConverters.javaMapToScalaMap(second)
      )
    )
  }

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

  /**
   * Prefix of properties forwarded to [[PartitioningConfig]].
   *
   * @since 0.1.0
   */
  final val PARTITIONING_PREFIX = "partitioning."
}
