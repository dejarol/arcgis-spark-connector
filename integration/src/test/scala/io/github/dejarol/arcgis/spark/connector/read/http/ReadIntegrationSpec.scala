package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.read.config.{QueryLayerConfig, ReadConfig}
import io.github.dejarol.arcgis.spark.connector.{ArcgisIntegrationSpec, ArcgisTableProvider, SparkSpec}

class ReadIntegrationSpec
  extends ArcgisIntegrationSpec
    with SparkSpec {

  private lazy val polygonLayerUri = integrationProperties.getProperty("ci.arcgis.test.polygonLayer.layerUri")

  describe("ARCGIS datasource") {
    describe(SHOULD) {
      describe("let users query a feature layer") {
        it("setting a where condition") {

          val df = spark.read.format(ArcgisTableProvider.SHORT_NAME)
            .option(ReadConfig.LAYER_URI_KEY, polygonLayerUri)
            .option(ReadConfig.QUERY_PREFIX + QueryLayerConfig.WHERE_KEY, "GEOID = '01'")
            .load()

          df.count() shouldBe 1
        }

        it("setting some objectIDs") {

          val df = spark.read.format(ArcgisTableProvider.SHORT_NAME)
            .option(ReadConfig.LAYER_URI_KEY, polygonLayerUri)
            .option(ReadConfig.QUERY_PREFIX + QueryLayerConfig.OBJECT_IDS_KEY, "1")
            .load()

          df.count() shouldBe 1
        }
      }
    }
  }
}
