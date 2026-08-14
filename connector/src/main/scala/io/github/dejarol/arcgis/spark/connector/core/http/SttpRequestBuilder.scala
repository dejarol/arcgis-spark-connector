package io.github.dejarol.arcgis.spark.connector.core.http

/**
 * Builds a concrete STTP request from a partial request template.
 *
 * @tparam L left (error) type of the decoded response body
 * @tparam R right (success) type of the decoded response body
 * @since 0.1.0
 */
trait SttpRequestBuilder[L, R] {

  /**
   * Completes a partial STTP request into a sendable request.
   *
   * @param initial partial request used as the starting point
   * @return a fully configured STTP request with body type `Either[L, R]`
   * @since 0.1.0
   */
  def build(initial: PReqType): EitherReq[L, R]
}

/**
 * Builds STTP requests whose decoded body is `Either[Throwable, R]`.
 *
 * @tparam R success type extracted from the response body
 * @since 0.1.0
 */
trait SttpEitherThrowableOrValueBuilder[R]
  extends SttpRequestBuilder[Throwable, R]
