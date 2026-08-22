package io.github.dejarol.arcgis.spark.connector.core.http.item

/**
 * TODO
 * @param id
 * @param url
 * @param name
 * @param title
 */
case class GetItemByIdResponse(
                                id: String,
                                url: String,
                                name: Option[String],
                                title: Option[String]
                              )
