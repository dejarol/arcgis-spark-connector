package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.http.{RequestBuilderIntegrationSpec, uriFromString}
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureServiceDefinition}
import org.scalatest.Inspectors

class GetFeatureServiceDefinitionRequestBuilderIntegrationSpec
  extends RequestBuilderIntegrationSpec
    with Inspectors {

  describe(anInstanceOf[GetFeatureServiceDefinitionRequestBuilder]) {
    describe(SHOULD) {
      it("get a feature service definition") {

        val body = sendRequestAndGetBody[FeatureServiceDefinition](
          GetFeatureServiceDefinitionRequestBuilder(
            uriFromString(
              integrationProperties.getProperty("ci.arcgis.test.polygonLayer.url")
            ), token
          )
        )

        body.layers should have size 3
      }
    }
  }
}