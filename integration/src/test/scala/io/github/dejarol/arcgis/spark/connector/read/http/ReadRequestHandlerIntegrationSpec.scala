package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.ArcgisIntegrationSpec
import io.github.dejarol.arcgis.spark.connector.core.models.EsriGeometryType
import io.github.dejarol.arcgis.spark.connector.read.QueryParameters

class ReadRequestHandlerIntegrationSpec
  extends ArcgisIntegrationSpec {

  private lazy val handler = ReadRequestHandler.withDefaultBackend()
  private lazy val polygonLayerUri = uriFromString(
    integrationProperties.getProperty("ci.arcgis.test.polygonLayer.layerUri")
  )

  describe(anInstanceOf[ReadRequestHandler]) {
    describe(SHOULD) {
      describe("get feature layer fields and geometry") {
        it("when outFields is not set") {

          val (fields, geometryType) = handler.getFeatureLayerFieldsAndGeometry(
            polygonLayerUri, None, None
          )

          fields shouldNot be (empty)
          geometryType shouldEqual EsriGeometryType.POLYGON
        }

        it("when outFields is set") {

          val outFields = Seq("geoid", "name")
          val (fields, geometryType) = handler.getFeatureLayerFieldsAndGeometry(
            polygonLayerUri, Some(outFields), None
          )

          fields should have size outFields.size
          fields.map(_.name.toLowerCase) should contain theSameElementsInOrderAs outFields
          geometryType shouldEqual EsriGeometryType.POLYGON
        }
      }

      describe("query a feature layer using post method") {
        it("applying a where condition") {

          val response = handler.queryUsingPost(
            polygonLayerUri, QueryParameters(
              where = Some("GEOID = '01'")
            ), None
          )

          response.features should have size 1
          val head = response.features.head
          head.geometry shouldBe empty
        }
      }
    }
  }
}
