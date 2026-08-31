package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.http.AsMultiPartSpec
import org.scalatest.OptionValues

class QueryLayerParametersSpec
  extends AsMultiPartSpec
    with OptionValues {

  import AsMultiPartSpec._

  private val where = "name = 'john'"
  private val outFields = Seq("name", "age")

  describe(anInstanceOf[QueryLayerParameters]) {
    describe(SHOULD) {
      it("create a copy with offset and count set") {

        val original = QueryLayerParameters()
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
            QueryLayerParameters.returnCountOnly(
              Some(where), None, None
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
            QueryLayerParameters.returnCountOnly(None, None, None)
          )

          parts should have size 2
          parts should contain key "returnCountOnly"
          parts("returnCountOnly") should contain value "true"
          parts should contain key "where"
          parts("where") should contain value QueryLayerParameters.DEFAULT_WHERE
        }

        it("with some objectIDs (but no where)") {

          val parts = convertPartsToMap(
            QueryLayerParameters.returnCountOnly(
              None, Some(Seq(1,2)), None
            )
          )

          parts should have size 3
          parts should contain key "returnCountOnly"
          parts("returnCountOnly") should contain value "true"
          parts should contain key "where"
          parts("where") should contain value QueryLayerParameters.DEFAULT_WHERE
          parts should contain key "objectIds"
          parts("objectIds") should contain value "1,2"
        }

        it("with some objectIDs and where") {

          val parts = convertPartsToMap(
            QueryLayerParameters.returnCountOnly(
              Some(where), Some(Seq(1,2)), None
            )
          )

          parts should have size 3
          parts should contain key "returnCountOnly"
          parts("returnCountOnly") should contain value "true"
          parts should contain key "where"
          parts("where") should contain value where
          parts should contain key "objectIds"
          parts("objectIds") should contain value "1,2"
        }

        it("with a token") {

          val parts = convertPartsToMap(
            QueryLayerParameters.returnCountOnly(
              None, None, Some("token")
            )
          )

          parts should have size 3
          parts should contain key "returnCountOnly"
          parts("returnCountOnly") should contain value "true"
          parts should contain key "where"
          parts("where") should contain value QueryLayerParameters.DEFAULT_WHERE
          parts should contain key "token"
          parts("token") should contain value "token"
        }
      }

      describe("for querying a layer") {
        it("setting where") {

          // [1] With a where
          val partsWithWhere = convertPartsToMap(
            QueryLayerParameters(where = Some(where))
          )

          partsWithWhere should contain key "where"
          partsWithWhere("where") should contain value where

          // [2] Without a where
          val partsWithoutWhere = convertPartsToMap(
            QueryLayerParameters()
          )

          partsWithoutWhere should contain key "where"
          partsWithoutWhere("where") should contain value QueryLayerParameters.DEFAULT_WHERE
        }

        it("settings objectIds") {

          // [1] With objectIds
          val partWithOIDs = convertPartsToMap(
            QueryLayerParameters(objectIDs = Some(Seq(1, 2, 3)))
          )

          partWithOIDs should contain key "objectIds"
          partWithOIDs("objectIds") should contain value "1,2,3"

          // [2] Without objectIds
          val partWithoutOIDs = convertPartsToMap(
            QueryLayerParameters()
          )

          partWithoutOIDs shouldNot contain key "objectIds"
        }

        it("settings outFields") {

          // [1] With outFields
          val partsWithFields = convertPartsToMap(
            QueryLayerParameters(outFields = Some(outFields))
          )

          partsWithFields should contain key "outFields"
          partsWithFields("outFields") should contain value outFields.mkString(",")

          // [2] Without outFields
          val partWithoutFields = convertPartsToMap(
            QueryLayerParameters()
          )

          partWithoutFields should contain key "outFields"
          partWithoutFields("outFields") should contain value QueryLayerParameters.DEFAULT_OUT_FIELDS
        }

        it("settings returnGeometry") {

          // [1] ReturnGeometry unset
          val partsUnset = convertPartsToMap(
            QueryLayerParameters()
          )

          partsUnset should contain key "returnGeometry"
          partsUnset("returnGeometry") should contain value QueryLayerParameters.DEFAULT_RETURN_GEOMETRY

          // [2] ReturnGeometry is set
          val partsSet = convertPartsToMap(
            QueryLayerParameters(returnGeometry = Some(true))
          )

          partsSet should contain key "returnGeometry"
          partsSet("returnGeometry") should contain value true
        }

        it("setting outSR") {

          // [1] OutSR unset
          val partsUnset = convertPartsToMap(
            QueryLayerParameters()
          )

          partsUnset shouldNot contain key "outSR"

          // [2] OutSR is set
          val partsSet = convertPartsToMap(
            QueryLayerParameters(outSR = Some(4326))
          )

          partsSet should contain key "outSR"
          partsSet("outSR") should contain value 4326
        }

        it("setting resultOffset and resultRecordCount") {

          // [1] Unset
          val partsUnset = convertPartsToMap(
            QueryLayerParameters()
          )

          partsUnset shouldNot contain key "resultOffset"
          partsUnset shouldNot contain key "resultRecordCount"

          // [2] Set
          val partsSet = convertPartsToMap(
            QueryLayerParameters(
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
