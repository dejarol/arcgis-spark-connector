package io.github.dejarol.arcgis.spark.connector.core.http.auth

import io.github.dejarol.arcgis.spark.connector.core.http._
import sttp.client4.multipart
import sttp.model.Uri

/**
 * Builds a POST multipart request for ArcGIS generateToken authentication.
 *
 * @param root  ArcGIS Online/enterprise root URL
 * @param parameters TODO
 * @since 0.1.0
 */
case class GenerateTokenRequestBuilder(
                                        root: Uri,
                                        parameters: GenerateTokenParameters
                                      )
  extends SttpEitherThrowableOrValueBuilder[GenerateTokenResponse] {

  import ResponseAsSuppliers._

  override def build(initial: PReqType): EitherReq[Throwable, GenerateTokenResponse] = {

    initial.post(
      root.addPath("generateToken")
    ).multipartBody(
      multipart("f", "json"),
      parameters.parts(): _*
    ).response(
      eitherThrowableOr[GenerateTokenResponse]().get()
    )
  }
}
