package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http._
import sttp.model.{Method, Uri}

/**
 * TODO
 * @param root
 * @param itemId
 * @param token
 */
case class GetItemByIdRequestBuilder(
                                      private val root: Uri,
                                      private val itemId: String,
                                      private val token: String
                                    )
  extends SttpEitherThrowableOrValueBuilder[GetItemByIdResponse] {

  override def build(initial: PReqType): EitherReq[Throwable, GetItemByIdResponse] = {

    initial.method(
      Method.GET, root.addPath(
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
