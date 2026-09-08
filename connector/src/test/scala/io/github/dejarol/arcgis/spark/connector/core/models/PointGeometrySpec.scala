package io.github.dejarol.arcgis.spark.connector.core.models

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JSONMixins}
import org.scalatest.OptionValues

class PointGeometrySpec
  extends BasicSpec
    with JSONMixins
    with OptionValues {

  private lazy val (x, y, wkid) = (1.2, 3.4, 5)

  describe(`object`[PointGeometry]) {
    describe(SHOULD) {
      describe("deserialize a point geometry from a JSON") {
        it("with spatial reference") {

          val json =
            f"""
               |{
               |  "x": $x,
               |  "y": $y,
               |  "spatialReference": {
               |    "wkid": $wkid
               |  }
               |}""".stripMargin

          val point = PointGeometry.fromJSON(asJValue(json))

          point.x shouldBe x
          point.y shouldBe y
          point.spatialReference.value shouldBe SpatialReference(Some(wkid), None)
        }

        it("without spatial reference") {

          val json =
            f"""
               |{
               |  "x": $x,
               |  "y": $y
               |}""".stripMargin

          val point = PointGeometry.fromJSON(asJValue(json))

          point.x shouldBe x
          point.y shouldBe y
          point.spatialReference shouldBe empty
        }
      }

      it("throw an exception for invalid JSON") {

        an[IllegalArgumentException] shouldBe thrownBy {
          PointGeometry.fromJSON(
            asJValue("{}")
          )
        }
      }
    }
  }
}
