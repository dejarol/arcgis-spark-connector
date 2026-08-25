package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.ArcgisIntegrationSpec
import io.github.dejarol.arcgis.spark.connector.core.models.EsriGeometryType
import io.github.dejarol.arcgis.spark.connector.read.QueryParameters
import io.github.dejarol.arcgis.spark.connector.read.models.QueryResponse
import org.scalatest.{Inspectors, OptionValues}

class ReadRequestHandlerIntegrationSpec
  extends ArcgisIntegrationSpec
    with OptionValues
      with Inspectors {

  private lazy val handler = ReadRequestHandler.withDefaultBackend()
  private lazy val polygonLayerUri = uriFromString(
    integrationProperties.getProperty("ci.arcgis.test.polygonLayer.layerUri")
  )

  /**
   * TODO
   * @param response
   * @param expectedGeometryType
   * @param expectedNumberOfFeatures
   * @param featuresShouldHaveGeometry
   * @param expectedFieldNames
   * @param expectedSR
   */
  private def assertQueryResponse(
                                   response: QueryResponse,
                                   expectedGeometryType: EsriGeometryType,
                                   expectedNumberOfFeatures: Int,
                                   featuresShouldHaveGeometry: Boolean,
                                   expectedFieldNames: Option[Seq[String]] = None,
                                   expectedSR: Option[Int] = None
                                 ): Unit = {

    // [1.1] Assert geometry type
    response.geometryType shouldEqual expectedGeometryType

    // [1.2] Assert some fields exist
    response.fields shouldNot be (empty)

    // [1.3] Assert expected fields exist
    expectedFieldNames.foreach {
      expected =>
        response.fieldNames should contain only (expected:_*)
    }

    // [1.4] Assert number of features
    response.features should have size expectedNumberOfFeatures

    // [1.5] Assert features have geometry
    if (featuresShouldHaveGeometry) {
      forAll(response.features) {
        feature =>
          feature.geometry shouldBe defined
          feature.geometry.value.`type`() shouldEqual expectedGeometryType
      }
    } else {
      forAll(response.features) {
        _.geometry shouldBe empty
      }
    }

    // [1.6] Assert content of spatialReference
    expectedSR.foreach {
      wkid =>
        response.spatialReference.wkid.value shouldBe wkid
        response.spatialReference.latestWkid.value shouldBe wkid
    }
  }

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
        it("applying a where condition, without returning the geometry") {

          val response = handler.queryUsingPost(
            polygonLayerUri, QueryParameters(
              where = Some("GEOID = '01'")
            ), None
          )

          assertQueryResponse(
            response, EsriGeometryType.POLYGON, 1, featuresShouldHaveGeometry = false
          )
        }

        it("applying a where condition, returning the geometry") {

          val response = handler.queryUsingPost(
            polygonLayerUri, QueryParameters(
              where = Some("GEOID = '01'"), returnGeometry = Some(true)
            ), None
          )

          assertQueryResponse(
            response, EsriGeometryType.POLYGON, 1, featuresShouldHaveGeometry = true
          )
        }

        it("returning only a subset of fields") {

          val outFields = Seq("GEOID", "NAME")
          val response = handler.queryUsingPost(
            polygonLayerUri, QueryParameters(
              where = Some("GEOID = '01'"), outFields = Some(outFields)
            ), None
          )

          assertQueryResponse(
            response, EsriGeometryType.POLYGON, 1,
            featuresShouldHaveGeometry = false,
            expectedFieldNames = Some(outFields)
          )
        }

        it("returning geometries with a different SR") {

          val response = handler.queryUsingPost(
            polygonLayerUri, QueryParameters(
              where = Some("GEOID = '01'"), returnGeometry = Some(true), outSR = Some(4326)
            ), None
          )

          assertQueryResponse(
            response, EsriGeometryType.POLYGON, 1,
            featuresShouldHaveGeometry = true,
            expectedSR = Some(4326)
          )
        }

        it("returning a specific slice of data") {

          val (offset, featureCount) = (0, 10)
          val response = handler.queryUsingPost(
            polygonLayerUri, QueryParameters(
              outFields = Some(Seq("GEOID", "NAME")),
              resultOffset = Some(offset),
              resultRecordCount = Some(featureCount)
            ), None
          )

          assertQueryResponse(
            response, EsriGeometryType.POLYGON,
            featureCount - offset,
            featuresShouldHaveGeometry = false,
            expectedFieldNames = Some(Seq("GEOID", "NAME"))
          )
        }
      }
    }
  }
}
