package io.github.dejarol.arcgis.spark.connector.core.models

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JSONMixins}
import org.json4s.native.JsonMethods
import org.scalatest.OptionValues

class PolygonGeometrySpec
  extends BasicSpec
    with JSONMixins
    with OptionValues {

  describe(`object`[PolygonGeometry]) {
    describe(SHOULD) {
      describe("deserialize a polygon from a JSON") {
        it("with a spatial reference") {

          val json =
          """
              |{
              | "rings": [
              |   [
              |     [1.2, 3.4]
              |   ]
              | ],
              | "spatialReference": {
              |   "wkid": 102100
              | }
              |}""".stripMargin

          val polygon = PolygonGeometry.fromJSON(asJValue(json))
          polygon.rings should have size 1
          polygon.spatialReference.value shouldBe SpatialReference(Some(102100), None)
        }

        it("without a spatial reference") {

          val json =
            """
              |{
              | "rings": [
              |   [
              |     [1.2, 3.4]
              |   ]
              | ]
              |}""".stripMargin

          val polygon = PolygonGeometry.fromJSON(asJValue(json))
          polygon.rings should have size 1
          polygon.spatialReference shouldBe empty
        }
      }

      it("throw an exception for invalid JSON") {

        an[IllegalArgumentException] shouldBe thrownBy {
          PolygonGeometry.fromJSON(
            asJValue("{}")
          )
        }
      }
    }
  }
}
