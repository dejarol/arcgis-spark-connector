package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.http.auth.GenerateTokenParameters
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import sttp.model.Uri

import java.time.Duration

/**
 * Authentication options used to request an ArcGIS generate-token response.
 *
 * @param properties configuration entries keyed by property name
 * @since 0.2.0
 */
case class AuthConfig(override protected val properties: CaseInsensitiveMap[String])
  extends BaseConfig(properties) {

  import AuthConfig._

  /**
   * Returns the ArcGIS root URL used for token generation, if set.
   *
   * @return the parsed root URI, or `None` if unset
   * @since 0.2.0
   */
  def rootUrl: Option[Uri] = getAs[Uri](ROOT_URL, PropertyConversions.ToUri)

  /**
   * Returns the account user name, if set.
   *
   * @return the user name, or `None` if unset
   * @since 0.2.0
   */
  def username: Option[String] = get(USERNAME)

  /**
   * Returns the account password, if set.
   *
   * @return the password, or `None` if unset
   * @since 0.2.0
   */
  def password: Option[String] = get(PASSWORD)

  /**
   * Returns the HTTP referer sent with the generate-token request, if set.
   *
   * @return the referer, or `None` if unset
   * @since 0.2.0
   */
  def referer: Option[String] = get(REFERER)

  /**
   * Returns the requested token lifetime in minutes.
   *
   * @return the configured lifetime, or `60` when unset
   * @since 0.2.0
   */
  def tokenExpirationMinutes: Int = {

    getAs[Int](
      TOKEN_EXPIRATION_MINUTES,
      PropertyConversions.ToInteger,
      60
    )
  }

  /**
   * Builds generate-token parameters when username, password, and referer are all set.
   *
   * @return the parameters, or `None` if any required credential is missing
   * @since 0.2.0
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

  /**
   * Property key for the ArcGIS root URL used for token generation.
   *
   * @since 0.2.0
   */
  final val ROOT_URL = "rootUrl"

  /**
   * Property key for the account user name.
   *
   * @since 0.2.0
   */
  final val USERNAME = "username"

  /**
   * Property key for the account password.
   *
   * @since 0.2.0
   */
  final val PASSWORD = "password"

  /**
   * Property key for the HTTP referer sent with the generate-token request.
   *
   * @since 0.2.0
   */
  final val REFERER = "referer"

  /**
   * Property key for the requested token lifetime in minutes.
   *
   * @since 0.2.0
   */
  final val TOKEN_EXPIRATION_MINUTES = "tokenExpirationMinutes"
}
