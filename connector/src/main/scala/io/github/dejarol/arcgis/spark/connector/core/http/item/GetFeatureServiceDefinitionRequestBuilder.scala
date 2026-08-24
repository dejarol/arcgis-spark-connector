package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http.{EitherReq, PReqType, ResponseAsSuppliers, SttpEitherThrowableOrValueBuilder}
import io.github.dejarol.arcgis.spark.connector.core.models.FeatureServiceDefinition
import sttp.model.Uri

/**
 * Builds a request that retrieves an ArcGIS feature service definition.
 *
 * @param serviceUri URI of the feature service
 * @param token      ArcGIS authentication token
 */
case class GetFeatureServiceDefinitionRequestBuilder(
                                                      private val serviceUri: Uri,
                                                      private val token: String
                                                    )
  extends SttpEitherThrowableOrValueBuilder[FeatureServiceDefinition] {

  override def build(initial: PReqType): EitherReq[Throwable, FeatureServiceDefinition] = {

    initial.get(
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
