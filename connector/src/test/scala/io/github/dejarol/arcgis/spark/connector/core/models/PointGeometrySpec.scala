package io.github.dejarol.arcgis.spark.connector.core.models

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec

class PointGeometrySpec
  extends BasicSpec {

  describe(anInstanceOf[PointGeometry]) {
    describe(SHOULD) {
      it("evaluate the geometry type") {

        val point = PointGeometry(1.23, 4.56, None)
        point.`type`() shouldEqual EsriGeometryType.POINT
        point.isAPoint shouldEqual true
        point.isAPolygon shouldEqual false
      }
    }
  }
}
