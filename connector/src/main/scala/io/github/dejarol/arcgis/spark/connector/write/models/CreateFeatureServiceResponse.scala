package io.github.dejarol.arcgis.spark.connector.write.models

/**
 * TODO
 * @param name
 * @param itemId
 * @param `type`
 * @param serviceUrl
 */
case class CreateFeatureServiceResponse(
                                         name: String,
                                         itemId: String,
                                         `type`: String,
                                         serviceUrl: String
                                       )
