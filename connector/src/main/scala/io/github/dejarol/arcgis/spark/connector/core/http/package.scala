package io.github.dejarol.arcgis.spark.connector.core

import sttp.client4.{PartialRequest, Request, Response, basicRequest}
import sttp.model.Uri

/**
 * Core HTTP helpers and type aliases for STTP requests used by the connector.
 *
 * @since 0.1.0
 */
package object http {

  type PRType = PartialRequest[Either[String, String]]
  type RType = Request[Either[String, String]]
  type RespType = Response[Either[String, String]]

  /**
   * Returns a fresh partial STTP request with the connector's default body type.
   *
   * @return an empty partial request ready to be customized
   * @since 0.1.0
   */
  def initialRequest(): PRType = basicRequest

  /**
   * Parses a URI from its string representation.
   *
   * @param string URI text to parse
   * @return the parsed STTP URI
   * @since 0.1.0
   */
  def uriFromString(string: String): Uri = Uri.unsafeParse(string)
}
