package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http.{EitherReq, PReqType, ResponseAsSuppliers, SttpEitherThrowableOrValueBuilder}
import sttp.model.{Method, Uri}

case class GetFeatureServiceDefinitionRequestBuilder(
                                                    private val serviceUri: Uri,
                                                    private val token: String
                                                    )
  extends SttpEitherThrowableOrValueBuilder[FeatureServiceDefinition] {

  override def build(initial: PReqType): EitherReq[Throwable, FeatureServiceDefinition] = {

    initial.method(
      Method.GET,
      serviceUri.addParams(
        Map(
          "f" -> "json",
          "token" -> token
        )
      )
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[FeatureServiceDefinition]().get()
    )
  }
}
