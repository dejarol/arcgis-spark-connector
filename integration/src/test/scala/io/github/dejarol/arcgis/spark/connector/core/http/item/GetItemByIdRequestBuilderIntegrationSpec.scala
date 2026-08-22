package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http.RequestBuilderIntegrationSpec

class GetItemByIdRequestBuilderIntegrationSpec
  extends RequestBuilderIntegrationSpec {

  describe(anInstanceOf[GetItemByIdRequestBuilderIntegrationSpec]) {
    describe(SHOULD) {
      it("get the details of an item") {

        // the item should be 'ACS Population (Latest)'
        val body = sendRequestAndGetBody[GetItemByIdResponse](
          GetItemByIdRequestBuilder(
            rootUri, "60c98f20a162416ea1725b94d7297f83", token
          )
        )

        body.id shouldNot be (null)
        body.url shouldNot be (null)
      }
    }
  }
}
