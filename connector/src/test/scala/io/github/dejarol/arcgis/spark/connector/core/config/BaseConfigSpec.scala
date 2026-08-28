package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

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

        it("as a typed optional with a default") {

          emptyConfig.getAs[Int]("k", PropertyConversions.ToInteger, -1) shouldBe -1

          val valid = createConfig("k", "27")
          valid.getAs[Int]("k", PropertyConversions.ToInteger, -1) shouldBe 27
          valid.unsafelyGetAs[Int]("k", PropertyConversions.ToInteger) shouldBe 27
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

          val properties = createConfig(
            Map(
              "k1" -> "v1",
              prefix + "k2" -> "v2"
            )
          ).propertiesStartingWithPrefix(prefix)
          properties shouldNot be (empty)
          properties should contain key "k2"
        }
      }
    }
  }
}

object BaseConfigSpec {

  /**
   * TODO
   * @return
   */
  private def createEmptyConfig(): BaseConfig = {

    new BaseConfig(
      CaseInsensitiveMap(
        Map.empty
      )
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
     CaseInsensitiveMap(
       Map(key -> value)
     )
   )
  }

  /**
   * Creates a configuration from one or more entries for the tests in this suite.
   *
   * @param map map of property entries
   * @return a configuration containing the given entries
   * @since 0.1.0
   */
  private def createConfig(map: Map[String, String]): BaseConfig = {

    new BaseConfig(
      CaseInsensitiveMap(map)
    )
  }
}
