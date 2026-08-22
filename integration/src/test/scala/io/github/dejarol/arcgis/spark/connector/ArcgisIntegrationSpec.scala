package io.github.dejarol.arcgis.spark.connector

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.http.auth.GenerateTokenRequestBuilder
import io.github.dejarol.arcgis.spark.connector.core.http.{initialRequest, uriFromString}
import sttp.client4.DefaultSyncBackend
import sttp.model.Uri

import java.time.Duration

/**
 * TODO
 */
trait ArcgisIntegrationSpec
  extends BasicSpec {

  import ArcgisIntegrationSpec._

  protected final lazy val propertiesSupplier = IntegrationPropertiesSuppliers.create()
  protected final lazy val rootUri: Uri = uriFromString(propertiesSupplier.root())
  protected final lazy val token: String = eitherCauseOrToken(
    rootUri, propertiesSupplier.username(), propertiesSupplier.password()
  ) match {
    case Right(value) => value
    case Left(value) => fail("Failed to get token for executing CI tests", value)
  }
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
}