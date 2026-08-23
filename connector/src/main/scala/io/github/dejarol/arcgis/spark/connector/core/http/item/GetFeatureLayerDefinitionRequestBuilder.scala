package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http._
import io.github.dejarol.arcgis.spark.connector.core.json.Customizations
import io.github.dejarol.arcgis.spark.connector.core.{EsriFieldType, EsriGeometryType}
import sttp.model.Uri

/**
 * TODO
 * @param featureServiceUri
 * @param layerId
 * @param token
 */
case class GetFeatureLayerDefinitionRequestBuilder(
                                                    private val featureServiceUri: Uri,
                                                    private val layerId: Int,
                                                    private val token: String
                                                  )
  extends SttpEitherThrowableOrValueBuilder[FeatureLayerDefinition] {

  override def build(initial: PReqType): EitherReq[Throwable, FeatureLayerDefinition] = {

    initial.get(
      featureServiceUri.addPath(
        String.valueOf(layerId)
      ).addParams(
        Map(
          "f" -> "json",
          "token" -> token
        )
      )
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[FeatureLayerDefinition](
        Customizations.serializerForEnumWithAPIName[EsriGeometryType](),
        Customizations.serializerForEnumWithAPIName[EsriFieldType]()
      ).get()
    )
  }
}
