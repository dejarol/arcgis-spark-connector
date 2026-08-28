package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.read.config.{QueryLayerConfig, ReadConfig}
import io.github.dejarol.arcgis.spark.connector.{ArcgisIntegrationSpec, ArcgisTableProvider, SparkSpec}
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

class ReadIntegrationSpec
  extends ArcgisIntegrationSpec
    with SparkSpec {

  describe("ARCGIS datasource") {
    describe(SHOULD) {
      describe("let users query a feature layer") {
        it("setting a where condition") {

          val readConfig = ReadConfig(
            CaseInsensitiveMap(
              Map(
                ReadConfig.LAYER_URI_KEY -> integrationProperties.getProperty("ci.arcgis.test.polygonLayer.layerUri"),
                ReadConfig.QUERY_PREFIX + QueryLayerConfig.WHERE_KEY -> "GEOID = '01'"
              )
            )
          )

          val x = readConfig.queryUsingPost(
            readConfig.queryLayerConfig.asQueryParameters
          )

          val a = 1
          val df = spark.read.format(ArcgisTableProvider.SHORT_NAME)
            .option(ReadConfig.LAYER_URI_KEY, integrationProperties.getProperty("ci.arcgis.test.polygonLayer.layerUri"))
            .option(ReadConfig.QUERY_PREFIX + QueryLayerConfig.WHERE_KEY, "GEOID = '01'")
            .load()

          df.show(false)
        }
      }
    }
  }
}
