package io.github.dejarol.arcgis.spark.connector.core.http.auth

import io.github.dejarol.arcgis.spark.connector.core.http.AsMultiParts
import sttp.client4.{BasicBodyPart, multipart}
import sttp.model.Part

import java.time.Duration

/**
 * TODO
 * @param username
 * @param password
 * @param referer
 * @param duration
 */
case class GenerateTokenParameters(
                                    username: String,
                                    password: String,
                                    referer: String,
                                    duration: Duration = Duration.ofHours(1)
                                  )
  extends AsMultiParts {

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
