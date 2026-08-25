package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.core.http.{EitherReq, PReqType, ResponseAsSuppliers, SttpEitherThrowableOrValueBuilder}
import io.github.dejarol.arcgis.spark.connector.read.FeatureLayerQueryParameters
import io.github.dejarol.arcgis.spark.connector.read.models.ReturnCountOnlyResponse
import sttp.client4.multipart
import sttp.model.Uri

/**
 * TODO
 * @param layerUri
 * @param queryParams
 */
case class ReturnCountOnlyRequestBuilder(
                                          private val layerUri: Uri,
                                          private val queryParams: FeatureLayerQueryParameters
                                        )
  extends SttpEitherThrowableOrValueBuilder[ReturnCountOnlyResponse] {

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
