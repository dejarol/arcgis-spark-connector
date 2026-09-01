package io.github.dejarol.arcgis.spark.connector.core.http.auth

import io.github.dejarol.arcgis.spark.connector.core.http.AsMultiParts
import sttp.client4.{BasicBodyPart, multipart}
import sttp.model.Part

import java.time.Duration

/**
 * Credentials and options sent as multipart form fields to an ArcGIS generateToken request.
 *
 * @param username account user name
 * @param password account password
 * @param referer  HTTP referer sent with the request
 * @param duration token lifetime; defaults to one hour
 * @since 0.1.0
 */
case class GenerateTokenParameters(
                                    username: String,
                                    password: String,
                                    referer: String,
                                    duration: Duration = Duration.ofHours(1)
                                  )
  extends AsMultiParts {

  /**
   * Returns the multipart form parts for a generateToken request.
   *
   * @return STTP multipart parts ready to attach to a request
   * @since 0.1.0
   */
  override def parts(): Seq[Part[BasicBodyPart]] = {

    Seq(
      multipart("username", username),
      multipart("password", password),
      multipart("client", "referer"),
      multipart("referer", referer),
      multipart("expiration", String.valueOf(duration.toMinutes))
    )
  }
}
