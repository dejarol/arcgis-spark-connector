package io.github.dejarol.arcgis.spark.connector.core.http.item

/**
 * Describes an ArcGIS portal item returned by an item lookup request.
 *
 * @param id    item identifier
 * @param url   URL of the item resource
 * @param name  optional internal item name
 * @param title optional display title
 */
case class GetItemByIdResponse(
                                id: String,
                                url: String,
                                name: Option[String],
                                title: Option[String]
                              )
