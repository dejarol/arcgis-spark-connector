package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.http._
import io.github.dejarol.arcgis.spark.connector.core.json.Customizations
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriFieldType, EsriGeometryType}
import io.github.dejarol.arcgis.spark.connector.read.models.QueryResponse
import sttp.client4.multipart
import sttp.model.Uri

/**
 * Builds a multipart POST request that queries an ArcGIS feature layer.
 *
 * @param layerUri       URI of the target layer
 * @param queryParameters parameters to include in the query
 * @param token          ArcGIS authentication token
 */
case class QueryFeatureLayerUsingPostRequestBuilder(
                                                     private val layerUri: Uri,
                                                     private val queryParameters: FeatureLayerQueryParameters,
                                                     private val token: Option[String]
                                                   )
  extends SttpEitherThrowableOrValueBuilder[QueryResponse] {

  override def build(initial: PReqType): EitherReq[Throwable, QueryResponse] = {

    // [1.1] Append 'query' to the layerUri
    val queryUri = layerUri.addPath("query")

    // [1.2] Append token if provided
    val queryUriWithToken = token.map {
      t => queryUri.addParam("token", t)
    }.getOrElse(queryUri)

    // [1.3] Build the request
    initial.post(queryUriWithToken).multipartBody(
      multipart("f", "json"),
      queryParameters.parts(): _*
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[QueryResponse](
        Customizations.serializerForGeometry(),
        Customizations.serializerForEnumWithAPIName[EsriGeometryType](),
        Customizations.serializerForEnumWithAPIName[EsriFieldType]()
      ).get()
    )
  }
}
