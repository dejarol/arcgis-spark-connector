package io.github.dejarol.arcgis.spark.connector.core.http.auth

/**
 * TODO
 * @param token
 * @param expires
 * @param ssl
 */
case class GenerateTokenResponse(token: String,
                                 expires: Long,
                                 ssl: Option[Boolean])
