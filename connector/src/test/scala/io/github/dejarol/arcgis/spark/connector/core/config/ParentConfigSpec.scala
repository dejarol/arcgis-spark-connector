package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}

import java.util

class ParentConfigSpec
  extends BasicSpec {

  import ParentConfigSpec._

  private lazy val emptyConfig: ParentConfig = createIOConfig(util.Collections.emptyMap())
  private lazy val singleEntryConfig: ParentConfig = createIOConfig("key", "value")

  describe(anInstanceOf[ParentConfig]) {
    describe(SHOULD) {
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

          val valid = createIOConfig("key", String.valueOf(8))
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
    }
  }
}

object ParentConfigSpec
  extends JavaMapMixins {

  /**
   * TODO
   * @param initial
   * @return
   */
  private def createIOConfig(
                              initial: util.Map[String, String]
                            ): ParentConfig = {

    new ParentConfig(initial)
  }

  /**
   * TODO
   * @param key
   * @param value
   * @return
   */
  private def createIOConfig(
                              key: String,
                              value: String
                            ): ParentConfig = {

   createIOConfig(
     createSimpleMap(key, value)
   )
  }
}