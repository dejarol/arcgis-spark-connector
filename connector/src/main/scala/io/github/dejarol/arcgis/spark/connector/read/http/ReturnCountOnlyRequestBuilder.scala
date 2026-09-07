package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.core.http.{EitherReq, PReqType, ResponseAsSuppliers, SttpEitherThrowableOrValueBuilder}
import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.models.ReturnCountOnlyResponse
import sttp.client4.multipart
import sttp.model.Uri

/**
 * Builds a multipart POST request that queries an ArcGIS feature layer for a feature count only.
 *
 * @param layerUri    URI of the target layer
 * @param queryParams query parameters that request a count-only response
 * @since 0.1.0
 */
case class ReturnCountOnlyRequestBuilder(
                                          private val layerUri: Uri,
                                          private val queryParams: QueryLayerParameters
                                        )
  extends SttpEitherThrowableOrValueBuilder[ReturnCountOnlyResponse] {

  /**
   * Completes a partial STTP request into a count-only query POST.
   *
   * @param initial partial request used as the starting point
   * @return a fully configured STTP request that decodes a [[ReturnCountOnlyResponse]]
   * @since 0.1.0
   */
  override def build(initial: PReqType): EitherReq[Throwable, ReturnCountOnlyResponse] = {

    initial.post(
      layerUri.addPath("query")
    ).multipartBody(
      multipart("f", "json"),
      queryParams.parts(): _*
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[ReturnCountOnlyResponse]().get()
    )
  }
}
