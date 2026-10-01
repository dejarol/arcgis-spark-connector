package io.github.dejarol.arcgis.spark.connector

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import sttp.model.Uri

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

  protected final lazy val polygonLayerUri = uriFromString(
    INTEGRATION_PROPERTIES.getProperty("ci.arcgis.test.polygonLayer.layerUri")
  )
  protected final lazy val pointLayerUri = uriFromString(
    INTEGRATION_PROPERTIES.getProperty("ci.arcgis.test.pointLayerWithDate.layerUri")
  )

  /**
   * Converts a string to a URI
   * @param string the raw URI string
   * @return the parsed URI
   */
  protected final def uriFromString(string: String): Uri = Uri.unsafeParse(string)
}

object ArcgisIntegrationSpec {

  lazy val SECRETS_SUPPLIER: IntegrationSecretsSupplier = IntegrationSecretsSuppliers.create()
  lazy val INTEGRATION_PROPERTIES: Properties = readIntegrationProperties()

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