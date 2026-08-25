package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.scalatest.OptionValues

class FeatureLayerQueryParametersSpec
  extends BasicSpec
    with OptionValues {

  describe(anInstanceOf[FeatureLayerQueryParameters]) {
    describe(SHOULD) {
      it("create a copy with offset and count set") {

        val original = FeatureLayerQueryParameters()
        original.resultOffset shouldBe empty
        original.resultRecordCount shouldBe empty

        val copy = original.withResultOffsetAndRecordCount(10, 20)
        copy.resultOffset.value shouldBe 10
        copy.resultRecordCount.value shouldBe 20
      }
    }

    describe("setup request parts") {
      it("for returning only the feature count") {

        // TODO
      }

      it("for querying a layer") {

        // TODO
      }
    }
  }
}
