package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http._
import io.github.dejarol.arcgis.spark.connector.core.json.Customizations
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriFieldType, EsriGeometryType, FeatureLayerDefinition}
import sttp.model.Uri

/**
 * Builds a request that retrieves an ArcGIS feature layer definition.
 *
 * @param featureLayerUri URI of the parent feature service
 * @param token             ArcGIS authentication token (required only for protected content)
 */
case class GetFeatureLayerDefinitionRequestBuilder(
                                                    private val featureLayerUri: Uri,
                                                    private val token: Option[String]
                                                  )
  extends SttpEitherThrowableOrValueBuilder[FeatureLayerDefinition] {

  override def build(initial: PReqType): EitherReq[Throwable, FeatureLayerDefinition] = {

    // Set parameters
    val defaultParams = Map("f" -> "json")
    val paramsMaybeWithToken = token.map {
      t => defaultParams + ("token" -> t)
    }.getOrElse(defaultParams)

    // Build request
    initial.get(
      featureLayerUri.addParams(paramsMaybeWithToken)
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[FeatureLayerDefinition](
        Customizations.serializerForEnumWithAPIName[EsriGeometryType](),
        Customizations.serializerForEnumWithAPIName[EsriFieldType]()
      ).get()
    )
  }
}
