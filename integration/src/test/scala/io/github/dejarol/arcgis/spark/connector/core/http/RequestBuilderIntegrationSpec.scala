package io.github.dejarol.arcgis.spark.connector.core.http

import io.github.dejarol.arcgis.spark.connector.ArcgisIntegrationSpec
import sttp.client4.{DefaultSyncBackend, SyncBackend}

/**
 * Base suite for integration tests of ArcGIS HTTP request builders.
 *
 * Provides a shared synchronous backend and a helper for retrieving response bodies.
 */
trait RequestBuilderIntegrationSpec
  extends ArcgisIntegrationSpec {

  protected final lazy val backend: SyncBackend = DefaultSyncBackend()

  /**
   * Sends a request and returns its successful response body.
   *
   * Fails the current test when the request returns an error.
   *
   * @param builder request builder to execute
   * @tparam R expected response body type
   * @return the successful response body
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
