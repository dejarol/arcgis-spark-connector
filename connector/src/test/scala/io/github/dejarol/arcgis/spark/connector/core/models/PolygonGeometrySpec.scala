package io.github.dejarol.arcgis.spark.connector.core.models

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec

class PolygonGeometrySpec
  extends BasicSpec {

  describe(anInstanceOf[PolygonGeometry]) {
    describe(SHOULD) {
      it("evaluate the geometry type") {

        val polygon = PolygonGeometry(Seq(Seq(Seq(1.23, 4.56), Seq(7.89, 10.11))), None)
        polygon.`type`() shouldEqual EsriGeometryType.POLYGON
        polygon.isAPoint shouldEqual false
        polygon.isAPolygon shouldEqual true
      }

      it("count polygons and vertices") {

        val polygon = PolygonGeometry(
          Seq(
            Seq(
              Seq(1.23, 4.56), Seq(7.89, 10.11)
            )
          ), None
        )

        polygon.numberOfPolygons shouldBe 1
        polygon.numberOfVertices shouldBe 2
      }
    }
  }
}
