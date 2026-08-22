package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.EsriGeometryType
import io.github.dejarol.arcgis.spark.connector.core.http.{EitherReq, PReqType, ResponseAsSuppliers, SttpEitherThrowableOrValueBuilder}
import io.github.dejarol.arcgis.spark.connector.core.json.Customizations
import org.json4s.DefaultFormats
import sttp.model.{Method, Uri}

/**
 * TODO
 * @param serviceUri
 * @param token
 */
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
      ResponseAsSuppliers.eitherThrowableOr[FeatureServiceDefinition](
        DefaultFormats + Customizations.serializerForEnumWithAPIName[EsriGeometryType]()
      ).get()
    )
  }
}
