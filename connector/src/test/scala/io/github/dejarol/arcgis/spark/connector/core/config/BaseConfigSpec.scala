package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}

class BaseConfigSpec
  extends BasicSpec {

  import BaseConfigSpec._

  private lazy val emptyConfig: BaseConfig = createEmptyConfig()
  private lazy val singleEntryConfig: BaseConfig = createConfig("key", "value")

  describe(anInstanceOf[BaseConfig]) {
    describe(SHOULD) {
      describe("evaluate whether") {
        it("it's empty") {

          emptyConfig.isEmpty shouldBe true
          singleEntryConfig.isEmpty shouldBe false
        }

        it("it contains a key") {

          emptyConfig.containsKey("k") shouldBe false
          singleEntryConfig.containsKey("key") shouldBe true
        }
      }

      describe("get a value") {
        it("as an optional") {

          emptyConfig.get("key") shouldBe empty
          singleEntryConfig.get("key") shouldBe Some("value")
        }

        it("letting a user set a default") {

          emptyConfig.getOrElse("key", "default") shouldBe "default"
          singleEntryConfig.getOrElse("key", "default") shouldBe "value"
        }

        it("as a typed optional") {

          emptyConfig.getAs[Int]("key", PropertyConversions.ToInteger) shouldBe empty

          val valid = createConfig("key", String.valueOf(8))
          valid.getAs[Int]("key", PropertyConversions.ToInteger) shouldBe Some(8)
          valid.unsafelyGetAs[Int]("key", PropertyConversions.ToInteger) shouldBe 8
        }
      }

      describe("raise an exception") {
        it("for missing properties") {

          a [NoSuchPropertyException] shouldBe thrownBy {
            emptyConfig.unsafelyGet("key")
          }
        }

        it("for invalid property values") {

          a [PropertyConversionException] shouldBe thrownBy {
            singleEntryConfig.unsafelyGetAs[Int]("key", PropertyConversions.ToInteger)
          }
        }
      }

      describe("all properties") {
        it("starting with a prefix") {

          val prefix = "prefix."
          emptyConfig.propertiesStartingWithPrefix(prefix) shouldBe empty
          singleEntryConfig.propertiesStartingWithPrefix(prefix) shouldBe empty

          val properties = createConfig(("k1", "v1"), (prefix + "k2", "v2")).propertiesStartingWithPrefix(prefix)
          properties shouldNot be (empty)
          properties should contain key "k2"
        }
      }
    }
  }
}

object BaseConfigSpec
  extends JavaMapMixins {

  /**
   * Creates an empty configuration for the tests in this suite.
   *
   * @return a configuration with no properties
   * @since 0.1.0
   */
  private def createEmptyConfig(): BaseConfig = {

    new BaseConfig(
      createEmptyMap()
    )
  }

  /**
   * Creates a configuration with a single property for the tests in this suite.
   *
   * @param key   property name
   * @param value property value
   * @return a configuration containing only that entry
   * @since 0.1.0
   */
  //noinspection SameParameterValue
  private def createConfig(
                              key: String,
                              value: String
                            ): BaseConfig = {

   new BaseConfig(
     createSingletonMap(key, value)
   )
  }

  /**
   * Creates a configuration from one or more entries for the tests in this suite.
   *
   * @param first  first property entry
   * @param others additional property entries
   * @return a configuration containing the given entries
   * @since 0.1.0
   */
  private def createConfig(
                          first: (String, String),
                          others: (String, String)*
                          ): BaseConfig = {

    new BaseConfig(
      createSimpleMap(first, others: _*)
    )
  }
}
