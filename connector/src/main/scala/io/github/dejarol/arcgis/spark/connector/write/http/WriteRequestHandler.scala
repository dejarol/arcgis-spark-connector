package io.github.dejarol.arcgis.spark.connector.write.http

import io.github.dejarol.arcgis.spark.connector.core.http.BaseRequestHandler
import io.github.dejarol.arcgis.spark.connector.core.http.auth.{GenerateTokenParameters, GenerateTokenRequestBuilder}
import sttp.client4.SyncBackend
import sttp.model.Uri

/**
 * TODO
 * @param backend STTP backend used to send requests
 */
case class WriteRequestHandler(override protected val backend: SyncBackend)
  extends BaseRequestHandler(backend) {

  /** TODO
   * @param rootUrl
   * @param parameters
   * @return
   */
  def requestToken(
                    rootUrl: Uri,
                    parameters: GenerateTokenParameters
                  ): String = {

    unsafelySend(
      GenerateTokenRequestBuilder(
        rootUrl, parameters
      )
    ).token
  }
}
