package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.ResponseAs

/**
 * Builds an STTP response decoder for a body of type `A`.
 *
 * @tparam A decoded response body type
 * @since 0.1.0
 */
trait ResponseAsBuilder[A] {

  /**
   * Creates the STTP response-as specification.
   *
   * @return a response decoder that yields values of type `A`
   * @since 0.1.0
   */
  def build(): ResponseAs[A]
}
