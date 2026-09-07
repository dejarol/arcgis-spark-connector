package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http.RequestBuilderIntegrationSpec
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureLayerDefinition}

class GetFeatureLayerDefinitionRequestBuilderIntegrationSpec
  extends RequestBuilderIntegrationSpec {

  describe(anInstanceOf[GetFeatureServiceDefinitionRequestBuilder]) {
    describe(SHOULD) {
      describe("retrieve the definition for") {
        it("a polygon layer") {

          val body = sendRequestAndGetBody[FeatureLayerDefinition](
            GetFeatureLayerDefinitionRequestBuilder(
              polygonLayerUri, None
            )
          )

          body.id shouldBe 0
          body.name.toLowerCase shouldBe "state"
          body.geometryType shouldBe EsriGeometryType.POLYGON
          body.fields shouldNot be (empty)
        }
      }
    }
  }
}
