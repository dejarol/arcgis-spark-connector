package io.github.dejarol.arcgis.spark.connector.write.http

import io.github.dejarol.arcgis.spark.connector.core.http.{EitherReq, PReqType, ResponseAsSuppliers, SttpEitherThrowableOrValueBuilder}
import io.github.dejarol.arcgis.spark.connector.core.json.Json4SUtils
import io.github.dejarol.arcgis.spark.connector.write.models.CreateFeatureServiceResponse
import sttp.client4.multipart
import sttp.model.Uri

case class CreateFeatureServiceRequestBuilder(
                                               private val root: Uri,
                                               private val request: CreateFeatureServiceRequest
                                             )
  extends SttpEitherThrowableOrValueBuilder[CreateFeatureServiceResponse] {

  override def build(initial: PReqType): EitherReq[Throwable, CreateFeatureServiceResponse] = {

    // https://[root]/content/users/[userName]/createService
    val uri = root.addPath("content", "users", request.username, "createService")
    initial.post(uri).multipartBody(
      multipart("f", "json"),
      multipart("token", request.token),
      multipart("outputType", "featureService"),
      multipart("createParameters", Json4SUtils.asJSONString(request.createParameters))
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[CreateFeatureServiceResponse]().get()
    )
  }
}
