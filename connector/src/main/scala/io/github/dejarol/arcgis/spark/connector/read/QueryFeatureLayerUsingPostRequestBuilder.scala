package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.http._
import sttp.client4.multipart
import sttp.model.Uri

/**
 * Builds a multipart POST request that queries an ArcGIS feature layer.
 *
 * @param queryUrl       URI of the layer query endpoint
 * @param queryParameters parameters to include in the query
 * @param token          ArcGIS authentication token
 */
case class QueryFeatureLayerUsingPostRequestBuilder(
                                                     private val queryUrl: Uri,
                                                     private val queryParameters: QueryParameters,
                                                     private val token: Option[String]
                                                   )
  extends SttpEitherThrowableOrValueBuilder[QueryResponse] {

  override def build(initial: PReqType): EitherReq[Throwable, QueryResponse] = {

    val queryUrlWithToken = token.map {
      t => queryUrl.addParam("token", t)
    }.getOrElse(queryUrl)

    initial.post(queryUrlWithToken).multipartBody(
      multipart("f", "json"),
      queryParameters.parts(): _*
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[QueryResponse]().get()
    )
  }
}
