package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec

class BaseConfigSpec
  extends ConfigSpec {

  private lazy val emptyConfig: BaseConfig = new BaseConfig(EMPTY_CIMAP)
  private lazy val singleEntryConfig: BaseConfig = new BaseConfig(
    createSingletonCIMap("key", "value")
  )

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

          val valid = new BaseConfig(
            createSingletonCIMap("key", String.valueOf(8))
          )
          valid.getAs[Int]("key", PropertyConversions.ToInteger) shouldBe Some(8)
          valid.unsafelyGetAs[Int]("key", PropertyConversions.ToInteger) shouldBe 8
        }

        it("as a typed optional with a default") {

          emptyConfig.getAs[Int]("k", PropertyConversions.ToInteger, -1) shouldBe -1

          val valid = new BaseConfig(
            createSingletonCIMap("k", "27")
          )
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

          val properties = new BaseConfig(
            createCIMap(
              Map(
                "k1" -> "v1",
                prefix + "k2" -> "v2"
              )
            )
          ).propertiesStartingWithPrefix(prefix)
          properties shouldNot be (empty)
          properties should contain key "k2"
        }
      }
    }
  }
}