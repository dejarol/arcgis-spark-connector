package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.http._
import sttp.client4.multipart
import sttp.model.{Method, Uri}

/**
 * Builds a multipart POST request that queries an ArcGIS feature layer.
 *
 * @param queryUrl       URI of the layer query endpoint
 * @param token          ArcGIS authentication token
 * @param queryParameters parameters to include in the query
 */
case class QueryUsingPostRequestBuilder(
                                         private val queryUrl: Uri,
                                         private val token: String,
                                         private val queryParameters: QueryParameters
                                       )
  extends SttpEitherThrowableOrValueBuilder[QueryResponse] {

  override def build(initial: PReqType): EitherReq[Throwable, QueryResponse] = {

    initial.method(
      Method.POST, queryUrl.addParam("token", token)
    ).multipartBody(
      multipart("f", "json"),
      queryParameters.parts(): _*
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[QueryResponse]().get()
    )
  }
}
