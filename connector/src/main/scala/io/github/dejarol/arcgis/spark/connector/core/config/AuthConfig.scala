package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.http.auth.GenerateTokenParameters
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import sttp.model.Uri

import java.time.Duration

/**
 * TODO
 * @param properties configuration entries keyed by property name
 */
case class AuthConfig(override protected val properties: CaseInsensitiveMap[String])
  extends BaseConfig(properties) {

  import AuthConfig._

  /**
   * TODO
   * @return
   */
  def rootUrl: Option[Uri] = getAs[Uri](ROOT_URL, PropertyConversions.ToUri)

  /**
   * TODO
   * @return
   */
  def username: Option[String] = get(USERNAME)

  /**
   * TODO
   * @return
   */
  def password: Option[String] = get(PASSWORD)

  /**
   * todo
   * @return
   */
  def referer: Option[String] = get(REFERER)

  /**
   * TODO
   * @return
   */
  def tokenExpirationMinutes: Int = {

    getAs[Int](
      TOKEN_EXPIRATION_MINUTES,
      PropertyConversions.ToInteger,
      60
    )
  }

  /**
   * TODO
   * @return
   */
  def generateTokenParameters: Option[GenerateTokenParameters] = {

    for {
      u <- username
      p <- password
      r <- referer
    } yield GenerateTokenParameters(
      u, p, r, Duration.ofMinutes(
        tokenExpirationMinutes
      )
    )
  }
}

object AuthConfig {

  final val ROOT_URL = "rootUrl"

  final val USERNAME = "username"

  final val PASSWORD = "password"

  final val REFERER = "referer"

  final val TOKEN_EXPIRATION_MINUTES = "tokenExpirationMinutes"
}
