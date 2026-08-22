package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http.RequestBuilderIntegrationSpec

class GetItemByIdRequestBuilderIntegrationSpec
  extends RequestBuilderIntegrationSpec {

  describe(anInstanceOf[GetItemByIdRequestBuilderIntegrationSpec]) {
    describe(SHOULD) {
      it("get the details of an item") {

        lazy val polygonLayerId = integrationProperties.getProperty("ci.arcgis.test.polygonLayer.id")
        lazy val polygonLayerUrl = integrationProperties.getProperty("ci.arcgis.test.polygonLayer.url")

        // the item should be 'ACS Population (Latest)'
        val body = sendRequestAndGetBody[GetItemByIdResponse](
          GetItemByIdRequestBuilder(
            rootUri, polygonLayerId, token
          )
        )

        body.id shouldBe polygonLayerId
        body.url.toLowerCase shouldBe polygonLayerUrl.toLowerCase
      }
    }
  }
}
