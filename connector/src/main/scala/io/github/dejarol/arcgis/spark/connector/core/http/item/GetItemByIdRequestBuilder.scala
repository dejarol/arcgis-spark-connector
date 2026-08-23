package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http._
import sttp.model.Uri

/**
 * Builds a request that retrieves an ArcGIS portal item by identifier.
 *
 * @param root   URI of the ArcGIS portal root
 * @param itemId identifier of the item to retrieve
 * @param token  ArcGIS authentication token
 */
case class GetItemByIdRequestBuilder(
                                      private val root: Uri,
                                      private val itemId: String,
                                      private val token: String
                                    )
  extends SttpEitherThrowableOrValueBuilder[GetItemByIdResponse] {

  override def build(initial: PReqType): EitherReq[Throwable, GetItemByIdResponse] = {

    initial.get(
      root.addPath(
        "content", "items", itemId
      ).addParams(
        Map(
          "f" -> "json",
          "token" -> token
        )
      )
    ).response(
      ResponseAsSuppliers.eitherThrowableOr[GetItemByIdResponse]().get()
    )
  }
}
