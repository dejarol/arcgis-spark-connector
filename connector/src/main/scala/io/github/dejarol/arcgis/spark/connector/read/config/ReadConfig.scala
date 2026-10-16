package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.JavaScalaConverters
import io.github.dejarol.arcgis.spark.connector.core.config.{AuthConfig, BaseConfig, ExtendableConfig, NoSuchPropertyException, PropertyConversions}
import io.github.dejarol.arcgis.spark.connector.core.http.{BackendFactory, DefaultSyncBackendFactory, DisabledCertValidationBackendFactory}
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureLayerField}
import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.http.ReadRequestHandler
import io.github.dejarol.arcgis.spark.connector.read.models.QueryLayerResponse
import org.apache.spark.internal.Logging
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
  extends BaseConfig(properties)
    with ExtendableConfig[ReadConfig]
      with Logging {

  import ReadConfig._

  /**
   * Returns a copy of this configuration with the given option set.
   *
   * @param key   option name
   * @param value option value
   * @return a copy that includes `key`
   * @since 0.2.0
   */
  override def withOption(key: String, value: String): ReadConfig = {

    this.copy(
      properties = properties + (key, value)
    )
  }

  /**
   * Returns the STTP backend factory selected by the certificate-validation option.
   *
   * @return [[DisabledCertValidationBackendFactory]] when disabled, otherwise [[DefaultSyncBackendFactory]]
   * @since 0.2.0
   */
  def backendFactory: BackendFactory = {

    val disableCertValidation = getAs[Boolean](
      DISABLE_CERT_VALIDATION, PropertyConversions.ToBoolean, false
    )
    if (disableCertValidation) {
      log.warn("Disabling certificate validation is highly discouraged. Use it at your own risk")
      DisabledCertValidationBackendFactory
    } else {
      DefaultSyncBackendFactory
    }
  }

  /**
   * Returns authentication options taken from properties prefixed with <b>auth.</b>
   *
   * @return an authentication configuration with the prefix stripped from each key
   * @since 0.2.0
   */
  def authConfig: AuthConfig = {

    AuthConfig(
      propertiesStartingWithPrefix(AUTH_PREFIX)
    )
  }

  /**
   * Returns a copy of this configuration with option <b>query.outFields</b> set to the list of
   * field names in the given schema.
   *
   * @param fields the schema of the table to scan
   * @return a copy whose <b>query.outFields</b> set
   * @since 0.1.0
   */

  def withOutFields(fields: Seq[String]): ReadConfig = {

    withOption(
      QUERY_PREFIX + QueryLayerConfig.OUT_FIELDS_KEY,
      fields.mkString(",")
    )
  }

  /**
   * Returns a copy of this configuration with option <b>query.returnGeometry</b> set.
   *
   * @param flag whether the query should include geometry
   * @return a copy whose <b>query.returnGeometry</b> option is `flag`
   * @since 0.2.0
   */
  def withReturnGeometry(flag: Boolean): ReadConfig = {

    withOption(
      QUERY_PREFIX + QueryLayerConfig.RETURN_GEOMETRY_KEY,
      String.valueOf(flag)
    )
  }

  /**
   * TODO
   * @param predicates
   * @return
   */
  def withWhere(predicates: Seq[String]): ReadConfig = {

    if (predicates.isEmpty) {
      this
    } else {

      val conditionFromPredicates = if (predicates.size.equals(1)) {
        predicates.head
      } else {
        predicates.map {
          p => f"($p)"
        }.mkString(" and ")
      }

      val updatedWhereCondition = queryLayerConfig.where.map {
        where => f"($where) and $conditionFromPredicates"
      }.getOrElse(conditionFromPredicates)

      withOption(
        QUERY_PREFIX + QueryLayerConfig.WHERE_KEY, updatedWhereCondition
      )
    }
  }

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

    val maybeToken = maybeRequestToken
    val config = QueryLayerConfig(
      propertiesStartingWithPrefix(QUERY_PREFIX)
    )

    maybeToken.map {
      config.withToken
    }.getOrElse(config)
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
   * TODO
   * @return
   */
  def pushdownPredicate: Boolean = {

    getAs[Boolean](
      PUSHDOWN_PREDICATE_KEY,
      PropertyConversions.ToBoolean,
      true
    )
  }

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
      ReadRequestHandler(
        backendFactory.buildBackend()
      )
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
        layerUri, queryLayerConfig.outFields, maybeRequestToken
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
        layerUri, queryLayerConfig.where, queryLayerConfig.objectIDs, maybeRequestToken
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
        layerUri, queryParameters
      )
    }
  }

  /**
   * Requests an ArcGIS token when authentication options are complete.
   *
   * @return the generated token, or `None` if root URL or credentials are missing
   * @since 0.2.0
   */
  private def maybeRequestToken: Option[String] = {

    val config = authConfig
    for {
      url <- config.rootUrl
      parameters <- config.generateTokenParameters
    } yield withRequestHandlerDo {
      _.requestToken(url, parameters)
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

  /**
   * Property key for disabling TLS certificate validation.
   *
   * @since 0.2.0
   */
  final val DISABLE_CERT_VALIDATION = "disableCertValidation"

  /**
   * Prefix of properties forwarded to [[AuthConfig]].
   *
   * @since 0.2.0
   */
  final val AUTH_PREFIX = "auth."

  /**
   * TODO
   */
  final val PUSHDOWN_PREDICATE_KEY = "pushdownPredicate"
}
