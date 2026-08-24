package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}
import org.scalatest.OptionValues

class QueryLayerConfigSpec
  extends BasicSpec
    with OptionValues {

  import QueryLayerConfigSpec._

  private lazy val emptyConfig = createEmptyConfig()

  describe(anInstanceOf[QueryLayerConfig]) {
    describe(SHOULD) {
      describe("extract query parameters like") {
        it("where") {

          emptyConfig.where shouldBe empty

          val expected = "name = 'john'"
          val actual = createSingletonConfig(QueryLayerConfig.WHERE_KEY, expected).where
          actual.value shouldBe expected
        }

        it("outFields") {

          emptyConfig.outFields shouldBe empty

          val expected = Seq("name", "age")
          val actual = createSingletonConfig(
            QueryLayerConfig.OUT_FIELDS_KEY, expected.mkString(",")
          ).outFields

          actual.value should contain theSameElementsAs expected
        }

        it("returnGeometry") {

          emptyConfig.returnGeometry shouldBe empty

          val expected = true
          val actual = createSingletonConfig(
            QueryLayerConfig.RETURN_GEOMETRY_KEY, String.valueOf(expected)
          ).returnGeometry
          actual.value shouldBe expected
        }

        it("outSR") {

          emptyConfig.outSR shouldBe empty

          val expected = 4326
          val actual = createSingletonConfig(
            QueryLayerConfig.OUT_SR_KEY, String.valueOf(expected)
          ).outSR
          actual.value shouldBe expected
        }
      }
    }
  }
}

object QueryLayerConfigSpec
  extends JavaMapMixins {

  /**
   * TODO
   * @return
   */
  private def createEmptyConfig(): QueryLayerConfig = {

    QueryLayerConfig(
      createEmptyMap()
    )
  }

  /**
   * TODO
   * @param key
   * @param value
   * @return
   */
  private def createSingletonConfig(key: String, value: String): QueryLayerConfig = {

    QueryLayerConfig(
      createSingletonMap(key, value)
    )
  }
}