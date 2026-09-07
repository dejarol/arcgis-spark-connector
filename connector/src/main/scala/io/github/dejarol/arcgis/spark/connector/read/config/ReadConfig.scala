package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.JavaScalaConverters
import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, NoSuchPropertyException, PropertyConversions}
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
  def layerUri: Uri = {

    getAs[Uri](LAYER_URI_KEY, PropertyConversions.ToUri).orElse(
      getAs[Uri]("path", PropertyConversions.ToUri)
    ).getOrElse {
      throw new NoSuchPropertyException(
        f"Neither 'path' or '$LAYER_URI_KEY' options were defined. " +
          f"Please set one of the two for reading data from ARCGIS"
      )
    }
  }

  /**
   * Returns query options taken from properties prefixed with <b>query.</b>
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
   * Returns partitioning options taken from properties prefixed with <b>partitioning.</b>
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
   * @return the feature count for the configured `where` clause and `objectIds`
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
   * Builds a read configuration from a Spark options map.
   *
   * @param map case-insensitive Spark options
   * @return a read configuration backed by `map`
   * @since 0.1.0
   */
  def fromCIMap(map: CaseInsensitiveStringMap): ReadConfig = {

    ReadConfig(
      CaseInsensitiveMap(
        JavaScalaConverters.javaMapToScalaMap(map)
      )
    )
  }

  /**
   * Builds a read configuration by merging two Spark option maps.
   *
   * Entries in `second` override entries in `first` when keys collide.
   *
   * @param first  base case-insensitive Spark options
   * @param second options overlaid on `first`
   * @return a read configuration backed by the merged maps
   * @since 0.1.0
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
