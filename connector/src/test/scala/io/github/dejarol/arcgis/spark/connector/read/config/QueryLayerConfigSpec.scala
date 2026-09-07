package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec
import org.scalatest.OptionValues

class QueryLayerConfigSpec
  extends ConfigSpec
    with OptionValues {

  private lazy val emptyConfig = QueryLayerConfig(EMPTY_CIMAP)

  describe(anInstanceOf[QueryLayerConfig]) {
    describe(SHOULD) {
      describe("extract query parameters like") {
        it("where") {

          emptyConfig.where shouldBe empty

          val expected = "name = 'john'"
          val actual = QueryLayerConfig(
            createSingletonCIMap(QueryLayerConfig.WHERE_KEY, expected)
          ).where
          actual.value shouldBe expected
        }

        it("objectIDs") {

          emptyConfig.objectIDs shouldBe empty

          val objectIds = Seq(1, 2)
          val actual = QueryLayerConfig(
            createSingletonCIMap(QueryLayerConfig.OBJECT_IDS_KEY, objectIds.mkString(","))
          ).objectIDs

          actual.value should contain theSameElementsAs objectIds
        }

        it("outFields") {

          emptyConfig.outFields shouldBe empty

          val expected = Seq("name", "age")
          val actual = QueryLayerConfig(
            createSingletonCIMap(
              QueryLayerConfig.OUT_FIELDS_KEY, expected.mkString(",")
            )
          ).outFields

          actual.value should contain theSameElementsAs expected
        }

        it("returnGeometry") {

          emptyConfig.returnGeometry shouldBe empty

          val expected = true
          val actual = QueryLayerConfig(
            createSingletonCIMap(
              QueryLayerConfig.RETURN_GEOMETRY_KEY, String.valueOf(expected)
            )
          ).returnGeometry
          actual.value shouldBe expected
        }

        it("outSR") {

          emptyConfig.outSR shouldBe empty

          val expected = 4326
          val actual = QueryLayerConfig(
            createSingletonCIMap(
              QueryLayerConfig.OUT_SR_KEY, String.valueOf(expected)
            )
          ).outSR
          actual.value shouldBe expected
        }
      }

      describe("enrich an existing configuration with") {
        it("some objectIDs") {

          val objIDs = Seq(0, 1, 2)
          emptyConfig.objectIDs shouldBe empty
          emptyConfig.withObjectIDs(
            objIDs
          ).objectIDs.value should contain theSameElementsAs objIDs
        }

        it("some outFields") {

          val outFields = Seq("name", "age")
          emptyConfig.outFields shouldBe empty
          emptyConfig.withOutFields(
            outFields
          ).outFields.value should contain theSameElementsAs outFields
        }
      }
    }
  }
}
