package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.SyncBackend

/**
 * Sends STTP requests through a synchronous backend and unwraps the response body.
 *
 * @param backend STTP backend used to send requests
 * @since 0.1.0
 */
class BaseRequestHandler(protected val backend: SyncBackend) {

  /**
   * Sends a request and returns its successful response body.
   *
   * @param request request builder to execute
   * @tparam R expected response body type
   * @return the successful response body
   * @throws Throwable the underlying throwable if the request body was a `Left`
   * @since 0.1.0
   */
  final def unsafelySend[R](request: SttpEitherThrowableOrValueBuilder[R]): R = {

    request.build(
      initialRequest()
    ).send(backend).body match {
      case Right(value) => value
      case Left(throwable) => throw throwable
    }
  }
}
