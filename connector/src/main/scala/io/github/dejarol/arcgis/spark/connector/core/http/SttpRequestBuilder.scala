package io.github.dejarol.arcgis.spark.connector.core.http

/**
 * Builds a concrete STTP request from a partial request template.
 *
 * @since 0.1.0
 */
trait SttpRequestBuilder {

  /**
   * Completes a partial STTP request into a sendable request.
   *
   * @param initial partial request used as the starting point
   * @return a fully configured STTP request
   * @since 0.1.0
   */
  def build(initial: PRType): RType

}
