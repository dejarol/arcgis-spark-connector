package io.github.dejarol.arcgis.spark.connector

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.http.auth.GenerateTokenRequestBuilder
import io.github.dejarol.arcgis.spark.connector.core.http.{initialRequest, uriFromString}
import sttp.client4.DefaultSyncBackend
import sttp.model.Uri

import java.time.Duration
import java.util.Properties
import scala.io.Source

/**
 * TODO
 */
trait ArcgisIntegrationSpec
  extends BasicSpec {

  import ArcgisIntegrationSpec._

  protected final lazy val secretsSupplier = IntegrationSecretsSuppliers.create()
  protected final lazy val rootUri: Uri = uriFromString(secretsSupplier.root())
  protected final lazy val token: String = eitherCauseOrToken(
    rootUri, secretsSupplier.username(), secretsSupplier.password()
  ) match {
    case Right(value) => value
    case Left(value) => fail("Failed to get token for executing CI tests", value)
  }

  protected final lazy val integrationProperties: Properties = readIntegrationProperties()
}

object ArcgisIntegrationSpec {

  /**
   * TODO
   * @param rootUri
   * @param username
   * @param password
   * @return
   */
  private def eitherCauseOrToken(
                                  rootUri: Uri,
                                  username: String,
                                  password: String
                                ): Either[Throwable, String] = {

    GenerateTokenRequestBuilder(
      rootUri,
      username,
      password,
      "http://localhost:6080",
      Duration.ofMinutes(15)
    ).build(
      initialRequest()
    ).send(
      DefaultSyncBackend()
    ).body.right.map(_.token)
  }

  /**
   * TODO
   * @return
   */
  private def readIntegrationProperties(): Properties = {

   val props = new Properties()
   props.load(Source.fromResource("integration.properties").reader())
   props
  }
}