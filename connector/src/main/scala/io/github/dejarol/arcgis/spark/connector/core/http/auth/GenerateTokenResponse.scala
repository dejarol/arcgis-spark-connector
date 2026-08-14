package io.github.dejarol.arcgis.spark.connector.core.http.auth

/**
 * Successful payload returned by an ArcGIS generateToken call.
 *
 * @param token   issued authentication token
 * @param expires token expiration instant as an epoch millis timestamp
 * @param ssl     optional flag indicating whether HTTPS is required
 * @since 0.1.0
 */
case class GenerateTokenResponse(token: String,
                                 expires: Long,
                                 ssl: Option[Boolean])
