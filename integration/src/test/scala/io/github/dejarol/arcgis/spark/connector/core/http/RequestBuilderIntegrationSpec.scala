package io.github.dejarol.arcgis.spark.connector.core.http

import io.github.dejarol.arcgis.spark.connector.ArcgisIntegrationSpec
import sttp.client4.{DefaultSyncBackend, SyncBackend}

/**
 * TODO
 */
trait RequestBuilderIntegrationSpec
  extends ArcgisIntegrationSpec {

  protected final lazy val backend: SyncBackend = DefaultSyncBackend()

  /**
   * TODO
   * @param builder
   * @tparam R
   * @return
   */
  protected final def sendRequestAndGetBody[R](builder: SttpEitherThrowableOrValueBuilder[R]): R = {

    builder.build(
      initialRequest()
    ).send(backend).body match {
      case Right(value) => value
      case Left(value) => fail("Failed to get response body", value)
    }
  }
}
