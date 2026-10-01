package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec
import org.scalatest.OptionValues

import java.time.Duration

class AuthConfigSpec
  extends ConfigSpec
    with OptionValues {

  private lazy val emptyConfig = AuthConfig(EMPTY_CIMAP)
  private lazy val (url, username, password, referer) = ("https://www.arcgis.com", "admin", "pw", "ref")

  describe(anInstanceOf[AuthConfig]) {
    describe(SHOULD) {
      describe("retrieve") {
        it("the root url") {

          emptyConfig.rootUrl shouldBe empty
          AuthConfig(
            createSingletonCIMap(AuthConfig.ROOT_URL, url)
          ).rootUrl.value shouldBe PropertyConversions.ToUri(url)
        }

        it("the username") {

          emptyConfig.username shouldBe empty
          AuthConfig(
            createSingletonCIMap(AuthConfig.USERNAME, username)
          ).username.value shouldBe username
        }

        it("the password") {

          emptyConfig.password shouldBe empty
          AuthConfig(
            createSingletonCIMap(AuthConfig.PASSWORD, password)
          ).password.value shouldBe password
        }

        it("the referer") {

          emptyConfig.referer shouldBe empty
          AuthConfig(
            createSingletonCIMap(AuthConfig.REFERER, referer)
          ).referer.value shouldBe referer
        }

        it("the token expiration") {

          emptyConfig.tokenExpirationMinutes shouldBe 60
          AuthConfig(
            createSingletonCIMap(AuthConfig.TOKEN_EXPIRATION_MINUTES, "5")
          ).tokenExpirationMinutes shouldBe 5
        }

        describe("parameters for requesting a token") {
          it("as an empty optional") {

            emptyConfig.generateTokenParameters shouldBe empty

            // Only 1 parameter is set: the overall object should be a None
            AuthConfig(
              createSingletonCIMap(AuthConfig.ROOT_URL, url)
            ).generateTokenParameters shouldBe empty
          }

          it("as a defined optional") {

            // All parameters are set, we expect the object to exist
            val maybeParameters = AuthConfig(
              createCIMap(
                Map(
                  AuthConfig.ROOT_URL -> url,
                  AuthConfig.USERNAME -> username,
                  AuthConfig.PASSWORD -> password,
                  AuthConfig.REFERER -> referer
                )
              )
            ).generateTokenParameters
            maybeParameters shouldBe defined
            val parameters = maybeParameters.get
            parameters.username shouldBe username
            parameters.password shouldBe password
            parameters.referer shouldBe referer
            parameters.duration shouldBe Duration.ofMinutes(60)
          }
        }
      }
    }
  }
}
