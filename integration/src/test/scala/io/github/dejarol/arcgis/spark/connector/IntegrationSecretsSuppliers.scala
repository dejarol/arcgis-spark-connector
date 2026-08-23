package io.github.dejarol.arcgis.spark.connector

import java.lang.{Boolean => JBoolean}
import java.util.Properties
import scala.io.Source
import scala.util.Try

/**
 * Creates integration-test credential suppliers for local and CI/CD environments.
 */
object IntegrationSecretsSuppliers {

  final val CI_CD_ENV_VAR = "IS_CI_CD_ENV"

  /**
   * Supplies integration-test credentials from environment variables.
   */
  private object EnvSupplier
    extends IntegrationSecretsSupplier {

    override def root(): String = getPropertyOfThrow("CI_ARCGIS_TESTING_ROOT")
    override def username(): String = getPropertyOfThrow("CI_ARCGIS_USERNAME")
    override def password(): String = getPropertyOfThrow("CI_ARCGIS_PASSWORD")

    /**
     * Reads a required environment variable.
     *
     * @param key environment-variable name
     * @return the configured value
     * @throws java.lang.IllegalStateException if the environment variable is not defined
     */
    private def getPropertyOfThrow(key: String): String = {

      sys.env.get(key) match {
        case Some(value) => value
        case None => throw new IllegalStateException(
          s"A ${this.getClass.getSimpleName} has been configured, " +
            s"but env property $key does not exist. Please set such property and then repeat the test")
      }
    }
  }

  /**
   * Supplies integration-test credentials from properties loaded from a file.
   *
 * Created internally by [[IntegrationSecretsSuppliers.create]].
 *
   * @param properties properties containing the required credentials
   */
  private case class FileSupplier(private val properties: Properties)
    extends IntegrationSecretsSupplier {

    override def root(): String = getPropertyOrThrow("ci.arcgis.root")
    override def username(): String = getPropertyOrThrow("ci.arcgis.username")
    override def password(): String = getPropertyOrThrow("ci.arcgis.password")

    private def getPropertyOrThrow(key: String): String = {

      Option(properties.getProperty(key)) match {
        case Some(value) => value
        case None => throw new IllegalStateException(
          s"A ${this.getClass.getSimpleName} has been configured, " +
            s"but property '$key' does not exist with the file. " +
            s"Please set such property on the file and then repeat the test"
        )
      }
    }
  }

  /**
   * Selects a credential supplier for the current execution environment.
   *
   * @return an environment-based supplier on CI/CD, otherwise a file-based supplier
   */
  final def create(): IntegrationSecretsSupplier = {

    // Detect if we're on CI/CD by reading an env variable
    val isCICDEnv = sys.env.get(CI_CD_ENV_VAR).exists(JBoolean.parseBoolean)
    if (isCICDEnv) {
      EnvSupplier
    } else {
      createSecretSupplier(".integration.secrets")
    }
  }

  /**
   * Creates a credential supplier from a local properties file.
   *
   * @param fileName path to the local secrets file
   * @return a supplier backed by the loaded properties
   * @throws java.lang.IllegalStateException if the secrets file cannot be loaded
   */
  //noinspection SameParameterValue
  private def createSecretSupplier(fileName: String): IntegrationSecretsSupplier = {

    Try {
      val properties = new Properties()
      properties.load(Source.fromFile(fileName).reader())
      properties
    }.toEither.right.map(FileSupplier) match {
      case Left(value) => throw new IllegalStateException(
        s"Could not load local secrets file $fileName. " +
          s"You should either create a file named $fileName at project root " +
          s"or set env variable $CI_CD_ENV_VAR to 'true' to retrieve properties from env variables", value)
      case Right(value) => value
    }
  }
}
