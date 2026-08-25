package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.core.http.BaseRequestHandler
import io.github.dejarol.arcgis.spark.connector.core.http.item.GetFeatureLayerDefinitionRequestBuilder
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureLayerField}
import io.github.dejarol.arcgis.spark.connector.read.models.{QueryResponse, ReturnCountOnlyResponse}
import io.github.dejarol.arcgis.spark.connector.read.{QueryFeatureLayerUsingPostRequestBuilder, FeatureLayerQueryParameters}
import sttp.client4.{DefaultSyncBackend, SyncBackend}
import sttp.model.Uri

/**
 * TODO
 * @param backend
 */
case class ReadRequestHandler(override protected val backend: SyncBackend)
  extends BaseRequestHandler(backend) {

  /**
   * TODO
   * @param layerUri
   * @param outFields
   * @param token
   * @return
   */
  def getFeatureLayerFieldsAndGeometry(
                                        layerUri: Uri,
                                        outFields: Option[Seq[String]],
                                        token: Option[String]
                                      ): (Seq[FeatureLayerField], EsriGeometryType) = {

    // [1.1] Retrieve the whole layer definition
    val featureLayerDefinition = unsafelySend(
      GetFeatureLayerDefinitionRequestBuilder(layerUri, token)
    )

    // [1.2] Filter the layer fields, if outFields is specified
    val allFeatureLayerFields = featureLayerDefinition.fields
    val maybeFilteredFeatureLayerFields = outFields.map {
      fields => allFeatureLayerFields.filter {
        field => fields.exists {
          _.equalsIgnoreCase(field.name)
        }
      }
    }.getOrElse(allFeatureLayerFields)

    // [1.3] Return the filtered layer fields
    (
      maybeFilteredFeatureLayerFields,
      featureLayerDefinition.geometryType
    )
  }

  /**
   * @param layerUri
   * @param queryParameters
   * @param token
   * @return
   */
  def queryUsingPost(
                      layerUri: Uri,
                      queryParameters: FeatureLayerQueryParameters,
                      token: Option[String]
                    ): QueryResponse = {

    unsafelySend(
      QueryFeatureLayerUsingPostRequestBuilder(
        layerUri, queryParameters, token
      )
    )
  }

  /**
   * TODO
   * @param layerUri
   * @param where
   * @return
   */
  def returnCountOnly(
                       layerUri: Uri,
                       where: Option[String]
                     ): ReturnCountOnlyResponse = {

    unsafelySend(
      ReturnCountOnlyRequestBuilder(
        layerUri, FeatureLayerQueryParameters.returnCountOnly(where)
      )
    )
  }
}

object ReadRequestHandler {

  /**
   * TODO
   * @return
   */
  def withDefaultBackend(): ReadRequestHandler = {

    new ReadRequestHandler(
      DefaultSyncBackend()
    )
  }
}
