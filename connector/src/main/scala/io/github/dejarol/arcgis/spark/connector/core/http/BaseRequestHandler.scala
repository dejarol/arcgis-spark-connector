package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.{DefaultSyncBackend, SyncBackend}

/**
 * TODO
 * @param backend
 */
class BaseRequestHandler(protected val backend: SyncBackend) {

  /**
   * TODO
   * @param request
   * @tparam R
   * @return
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
