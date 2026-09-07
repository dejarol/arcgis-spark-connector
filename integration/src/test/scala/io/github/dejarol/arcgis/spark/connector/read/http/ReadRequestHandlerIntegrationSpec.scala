package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.ArcgisIntegrationSpec
import io.github.dejarol.arcgis.spark.connector.core.models.EsriGeometryType
import io.github.dejarol.arcgis.spark.connector.read.QueryLayerParameters
import io.github.dejarol.arcgis.spark.connector.read.models.QueryLayerResponse
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
   * Asserts that a query response matches the expected geometry, feature count, and field set.
   *
   * @param response                   query response to inspect
   * @param expectedGeometryType       expected geometry type of the features
   * @param expectedNumberOfFeatures   expected number of features
   * @param featuresShouldHaveGeometry whether each feature must include a geometry
   * @param expectedFieldNames         expected field names when a subset was requested
   * @param expectedSR                 expected spatial reference WKID when set
   * @since 0.1.0
   */
  private def assertQueryResponse(
                                   response: QueryLayerResponse,
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
            polygonLayerUri, QueryLayerParameters(
              where = Some("GEOID = '01'")
            ), None
          )

          assertQueryResponse(
            response, EsriGeometryType.POLYGON, 1, featuresShouldHaveGeometry = false
          )
        }

        it("applying a where condition, returning the geometry") {

          val response = handler.queryUsingPost(
            polygonLayerUri, QueryLayerParameters(
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
            polygonLayerUri, QueryLayerParameters(
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
            polygonLayerUri, QueryLayerParameters(
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
            polygonLayerUri, QueryLayerParameters(
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

        describe("returning count only") {
          it("with a where") {

            val response = handler.returnCountOnly(
              polygonLayerUri, where = Some("GEOID = '01'"), None, None
            )
            response.count shouldBe 1
          }

          it("without a where") {

            val response = handler.returnCountOnly(
              polygonLayerUri, None, None, None
            )
            response.count shouldBe 52
          }

          it("with some objectIDs") {

            val response = handler.returnCountOnly(
              polygonLayerUri, None, Some(Seq(1, 2, 3)), None
            )
            response.count shouldBe 3
          }

          it("with some objectIDs and a where") {

            val response = handler.returnCountOnly(
              polygonLayerUri, Some("GEOID = '01'"), Some(Seq(1, 2, 3)), None
            )
            response.count shouldBe 1
          }
        }
      }
    }
  }
}
