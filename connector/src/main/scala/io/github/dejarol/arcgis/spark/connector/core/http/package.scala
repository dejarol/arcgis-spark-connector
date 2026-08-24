package io.github.dejarol.arcgis.spark.connector.core

import sttp.client4.{PartialRequest, Request, basicRequest}

/**
 * Core HTTP helpers and type aliases for STTP requests used by the connector.
 *
 * @since 0.1.0
 */
package object http {

  type PReqType = PartialRequest[Either[String, String]]
  type EitherReq[L, R] = Request[Either[L, R]]

  /**
   * Returns a fresh partial STTP request with the connector's default body type.
   *
   * @return an empty partial request ready to be customized
   * @since 0.1.0
   */
  def initialRequest(): PReqType = basicRequest
}
