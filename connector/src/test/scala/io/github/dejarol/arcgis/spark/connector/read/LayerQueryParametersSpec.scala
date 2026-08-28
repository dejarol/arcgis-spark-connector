package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.http.AsMultiPartSpec
import org.scalatest.OptionValues

class LayerQueryParametersSpec
  extends AsMultiPartSpec
    with OptionValues {

  import AsMultiPartSpec._

  private val where = "name = 'john'"
  private val outFields = Seq("name", "age")

  describe(anInstanceOf[LayerQueryParameters]) {
    describe(SHOULD) {
      it("create a copy with offset and count set") {

        val original = LayerQueryParameters()
        original.resultOffset shouldBe empty
        original.resultRecordCount shouldBe empty

        val copy = original.withResultOffsetAndRecordCount(10, 20)
        copy.resultOffset.value shouldBe 10
        copy.resultRecordCount.value shouldBe 20
      }
    }

    describe("setup request parts") {
      describe("for returning only the feature count") {
        it("with a where") {

          val parts = convertPartsToMap(
            LayerQueryParameters.returnCountOnly(
              Some(where)
            )
          )

          parts should have size 2
          parts should contain key "returnCountOnly"
          parts("returnCountOnly") should contain value "true"
          parts should contain key "where"
          parts("where") should contain value where
        }

        it("without a where") {

          val parts = convertPartsToMap(
            LayerQueryParameters.returnCountOnly(None)
          )

          parts should have size 2
          parts should contain key "returnCountOnly"
          parts("returnCountOnly") should contain value "true"
          parts should contain key "where"
          parts("where") should contain value LayerQueryParameters.DEFAULT_WHERE
        }
      }

      describe("for querying a layer") {
        it("setting where") {

          // [1] With a where
          val partsWithWhere = convertPartsToMap(
            LayerQueryParameters(where = Some(where))
          )

          partsWithWhere should contain key "where"
          partsWithWhere("where") should contain value where

          // [2] Without a where
          val partsWithoutWhere = convertPartsToMap(
            LayerQueryParameters()
          )

          partsWithoutWhere should contain key "where"
          partsWithoutWhere("where") should contain value LayerQueryParameters.DEFAULT_WHERE
        }

        it("settings outFields") {

          // [1] With outFields
          val partsWithFields = convertPartsToMap(
            LayerQueryParameters(outFields = Some(outFields))
          )

          partsWithFields should contain key "outFields"
          partsWithFields("outFields") should contain value outFields.mkString(",")

          // [2] Without outFields
          val partWithoutFields = convertPartsToMap(
            LayerQueryParameters()
          )

          partWithoutFields should contain key "outFields"
          partWithoutFields("outFields") should contain value LayerQueryParameters.DEFAULT_OUT_FIELDS
        }

        it("settings returnGeometry") {

          // [1] ReturnGeometry unset
          val partsUnset = convertPartsToMap(
            LayerQueryParameters()
          )

          partsUnset should contain key "returnGeometry"
          partsUnset("returnGeometry") should contain value LayerQueryParameters.DEFAULT_RETURN_GEOMETRY

          // [2] ReturnGeometry is set
          val partsSet = convertPartsToMap(
            LayerQueryParameters(returnGeometry = Some(true))
          )

          partsSet should contain key "returnGeometry"
          partsSet("returnGeometry") should contain value true
        }

        it("setting outSR") {

          // [1] OutSR unset
          val partsUnset = convertPartsToMap(
            LayerQueryParameters()
          )

          partsUnset shouldNot contain key "outSR"

          // [2] OutSR is set
          val partsSet = convertPartsToMap(
            LayerQueryParameters(outSR = Some(4326))
          )

          partsSet should contain key "outSR"
          partsSet("outSR") should contain value 4326
        }

        it("setting resultOffset and resultRecordCount") {

          // [1] Unset
          val partsUnset = convertPartsToMap(
            LayerQueryParameters()
          )

          partsUnset shouldNot contain key "resultOffset"
          partsUnset shouldNot contain key "resultRecordCount"

          // [2] Set
          val partsSet = convertPartsToMap(
            LayerQueryParameters(
              resultOffset = Some(10),
              resultRecordCount = Some(20)
            )
          )

          partsSet should contain key "resultOffset"
          partsSet("resultOffset") should contain value 10
          partsSet should contain key "resultRecordCount"
          partsSet("resultRecordCount") should contain value 20
        }
      }
    }
  }
}
