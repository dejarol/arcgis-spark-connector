package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.core.http.BaseRequestHandler
import io.github.dejarol.arcgis.spark.connector.core.http.item.GetFeatureLayerDefinitionRequestBuilder
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureLayerField}
import io.github.dejarol.arcgis.spark.connector.read.models.{QueryLayerResponse, ReturnCountOnlyResponse}
import io.github.dejarol.arcgis.spark.connector.read.{QueryLayerUsingPostRequestBuilder, QueryLayerParameters}
import sttp.client4.{DefaultSyncBackend, SyncBackend}
import sttp.model.Uri

/**
 * Sends read-related HTTP requests against an ArcGIS feature layer.
 *
 * @param backend STTP backend used to send requests
 * @since 0.1.0
 */
case class ReadRequestHandler(override protected val backend: SyncBackend)
  extends BaseRequestHandler(backend) {

  /**
   * Fetches the feature layer's fields and geometry type.
   *
   * When `outFields` is set, only fields whose names match (case-insensitive) are returned.
   *
   * @param layerUri  URI of the feature layer
   * @param outFields optional output field names used to filter the layer definition
   * @param token     optional ArcGIS authentication token
   * @return the (possibly filtered) layer fields and the layer geometry type
   * @since 0.1.0
   */
  def getFeatureLayerFieldsAndGeometry(
                                        layerUri: Uri,
                                        outFields: Option[Seq[String]],
                                        token: Option[String]
                                      ): (Seq[FeatureLayerField], EsriGeometryType) = {

    // [1.1] Retrieve the whole layer definition
    val featureLayerDefinition = unsafelySend(
      GetFeatureLayerDefinitionRequestBuilder(layerUri, token)
    )

    // [1.2] Filter the layer fields, if outFields is specified
    val allFeatureLayerFields = featureLayerDefinition.fields
    val maybeFilteredFeatureLayerFields = outFields.map {
      fields => allFeatureLayerFields.filter {
        field => fields.exists {
          _.equalsIgnoreCase(field.name)
        }
      }
    }.getOrElse(allFeatureLayerFields)

    // [1.3] Return the filtered layer fields
    (
      maybeFilteredFeatureLayerFields,
      featureLayerDefinition.geometryType
    )
  }

  /**
   * Queries the feature layer using an HTTP POST.
   *
   * @param layerUri        URI of the feature layer
   * @param queryParameters multipart query parameters sent with the request
   * @param token           optional ArcGIS authentication token
   * @return the query response body
   * @since 0.1.0
   */
  def queryUsingPost(
                      layerUri: Uri,
                      queryParameters: QueryLayerParameters,
                      token: Option[String]
                    ): QueryLayerResponse = {

    unsafelySend(
      QueryLayerUsingPostRequestBuilder(
        layerUri, queryParameters, token
      )
    )
  }

  /**
   * Queries the feature layer for the number of matching features.
   *
   * @param layerUri  URI of the feature layer
   * @param where     optional ArcGIS `where` clause
   * @param objectIds optional object IDs that further restrict the count
   * @param token     optional ArcGIS authentication token
   * @return a response containing the matching feature count
   * @since 0.1.0
   */
  def returnCountOnly(
                       layerUri: Uri,
                       where: Option[String],
                       objectIds: Option[Seq[Int]],
                       token: Option[String]
                     ): ReturnCountOnlyResponse = {

    unsafelySend(
      ReturnCountOnlyRequestBuilder(
        layerUri, QueryLayerParameters.returnCountOnly(
          where, objectIds, token
        )
      )
    )
  }
}

object ReadRequestHandler {

  /**
   * Creates a handler that uses STTP's default synchronous backend.
   *
   * @return a request handler backed by the default STTP backend
   * @since 0.1.0
   */
  def withDefaultBackend(): ReadRequestHandler = {

    new ReadRequestHandler(
      DefaultSyncBackend()
    )
  }
}
