package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec

import java.util

class IOConfigSpec
  extends BasicSpec {

  import IOConfigSpec._

  private lazy val emptyConfig: IOConfig = createIOConfig(util.Collections.emptyMap())
  private lazy val singleEntryConfig: IOConfig = createIOConfig("key", "value")

  describe(anInstanceOf[IOConfig]) {
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

object IOConfigSpec {

  /**
   * TODO
   * @param initial
   * @return
   */
  private def createIOConfig(
                              initial: util.Map[String, String]
                            ): IOConfig = {

    new IOConfig(initial)
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
                            ): IOConfig = {

   createIOConfig(
     util.Collections.singletonMap(key, value)
   )
  }
}