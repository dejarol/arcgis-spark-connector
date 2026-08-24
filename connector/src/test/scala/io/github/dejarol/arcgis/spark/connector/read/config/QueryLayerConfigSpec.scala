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
   * Creates an empty query-layer configuration for the tests in this suite.
   *
   * @return a query-layer configuration with no properties
   * @since 0.1.0
   */
  private def createEmptyConfig(): QueryLayerConfig = {

    QueryLayerConfig(
      createEmptyMap()
    )
  }

  /**
   * Creates a query-layer configuration with a single property for the tests in this suite.
   *
   * @param key   property name
   * @param value property value
   * @return a query-layer configuration containing only that entry
   * @since 0.1.0
   */
  private def createSingletonConfig(key: String, value: String): QueryLayerConfig = {

    QueryLayerConfig(
      createSingletonMap(key, value)
    )
  }
}
