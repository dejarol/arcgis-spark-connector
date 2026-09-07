package io.github.dejarol.arcgis.spark.connector

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.http.auth.{GenerateTokenParameters, GenerateTokenRequestBuilder}
import io.github.dejarol.arcgis.spark.connector.core.http.initialRequest
import sttp.client4.DefaultSyncBackend
import sttp.model.Uri

import java.time.Duration
import java.util.Properties
import scala.io.Source

/**
 * Base suite for integration tests that communicate with an ArcGIS deployment.
 *
 * Provides shared credentials, authentication and integration-test configuration.
 */
trait ArcgisIntegrationSpec
  extends BasicSpec {

  import ArcgisIntegrationSpec._

  protected final lazy val secretsSupplier = IntegrationSecretsSuppliers.create()
  protected final lazy val rootUri: Uri = Uri.unsafeParse(secretsSupplier.root())
  protected final lazy val token: String = eitherCauseOrToken(
    rootUri, secretsSupplier.username(), secretsSupplier.password()
  ) match {
    case Right(value) => value
    case Left(value) => fail("Failed to get token for executing CI tests", value)
  }

  protected final lazy val integrationProperties: Properties = readIntegrationProperties()
  protected final lazy val polygonLayerUri = uriFromString(
    integrationProperties.getProperty("ci.arcgis.test.polygonLayer.layerUri")
  )
  protected final lazy val pointLayerUri = uriFromString(
    integrationProperties.getProperty("ci.arcgis.test.pointLayerWithDate.layerUri")
  )

  /**
   * Converts a string to a URI
   * @param string the raw URI string
   * @return the parsed URI
   */
  protected final def uriFromString(string: String): Uri = Uri.unsafeParse(string)
}

object ArcgisIntegrationSpec {

  /**
   * Requests an authentication token for integration tests.
   *
   * @param rootUri  URI of the ArcGIS deployment root
   * @param username ArcGIS account user name
   * @param password ArcGIS account password
   * @return either the request failure or the generated token
   */
  private def eitherCauseOrToken(
                                  rootUri: Uri,
                                  username: String,
                                  password: String
                                ): Either[Throwable, String] = {

    GenerateTokenRequestBuilder(
      rootUri,
      GenerateTokenParameters(
        username, password, "http://localhost:6080", Duration.ofMinutes(15)
      )
    ).build(
      initialRequest()
    ).send(
      DefaultSyncBackend()
    ).body.right.map(_.token)
  }

  /**
   * Loads the shared integration-test configuration from the classpath.
   *
   * @return the loaded integration-test properties
   */
  private def readIntegrationProperties(): Properties = {

   val props = new Properties()
   props.load(Source.fromResource("integration.properties").reader())
   props
  }
}