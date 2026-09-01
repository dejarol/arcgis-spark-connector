package io.github.dejarol.arcgis.spark.connector.core.http.auth

import io.github.dejarol.arcgis.spark.connector.core.http._
import sttp.client4.multipart
import sttp.model.Uri

/**
 * Builds a POST multipart request for ArcGIS generateToken authentication.
 *
 * @param root       ArcGIS Online/enterprise root URL
 * @param parameters credentials and options sent with the generateToken request
 * @since 0.1.0
 */
case class GenerateTokenRequestBuilder(
                                        root: Uri,
                                        parameters: GenerateTokenParameters
                                      )
  extends SttpEitherThrowableOrValueBuilder[GenerateTokenResponse] {

  import ResponseAsSuppliers._

  /**
   * Completes a partial STTP request into a generateToken POST.
   *
   * @param initial partial request used as the starting point
   * @return a fully configured STTP request that decodes a [[GenerateTokenResponse]]
   * @since 0.1.0
   */
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
