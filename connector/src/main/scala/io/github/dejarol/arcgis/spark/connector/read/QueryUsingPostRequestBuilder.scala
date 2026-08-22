package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.http._
import sttp.client4.multipart
import sttp.model.{Method, Uri}

/**
 * TODO
 * @param queryUrl
 * @param token
 * @param queryParameters
 */
case class QueryUsingPostRequestBuilder(
                                         private val queryUrl: Uri,
                                         private val token: String,
                                         private val queryParameters: QueryParameters
                                       )
  extends SttpEitherThrowableOrValueBuilder[QueryResponse]
    with SttpMultiPartMixins {

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
