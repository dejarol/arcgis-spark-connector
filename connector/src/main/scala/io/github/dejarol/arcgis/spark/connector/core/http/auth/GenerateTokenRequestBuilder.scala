package io.github.dejarol.arcgis.spark.connector.core.http.auth

import io.github.dejarol.arcgis.spark.connector.core.http.{SttpMultiPartMixins, SttpRequestBuilder, PRType, RType}
import sttp.model.{Method, Uri}

import java.time.Duration

/**
 * Builds a POST multipart request for ArcGIS generateToken authentication.
 *
 * @param authUri  generateToken endpoint URI
 * @param username account user name
 * @param password account password
 * @param referer  HTTP referer sent as the token client identity
 * @param duration requested token lifetime
 * @since 0.1.0
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
