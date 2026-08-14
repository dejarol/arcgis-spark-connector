package io.github.dejarol.arcgis.spark.connector.core.http.auth

import io.github.dejarol.arcgis.spark.connector.core.http.{SttpMultiPartMixins, SttpRequestBuilder, PRType, RType}
import sttp.model.{Method, Uri}

import java.time.Duration

/**
 * TODO
 * @param authUri
 * @param username
 * @param password
 * @param referer
 * @param duration
 */
case class GenerateTokenRequestBuilder(
                                        private[auth] val authUri: Uri,
                                        private[auth] val username: String,
                                        private[auth] val password: String,
                                        private[auth] val referer: String,
                                        private[auth] val duration: Duration = Duration.ofHours(1)
                                      )
  extends SttpRequestBuilder
    with SttpMultiPartMixins {

  override def build(initial: PRType): RType = {

    initial.method(
      Method.POST, authUri
    ).multipartBody(
      createParts(
        Map(
          "f" -> "json",
          "username" -> username,
          "password" -> password,
          "client" -> "referer",
          "referer" -> referer,
          "expiration" -> String.valueOf(duration.toMinutes)
        )
      )
    )
  }
}
