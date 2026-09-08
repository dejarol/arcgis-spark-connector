package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriFieldType, FeatureLayerField}
import io.github.dejarol.arcgis.spark.connector.read.models.ArcgisFeature

class ArcgisFeatureToInternalRowEncoderImplSpec
  extends BasicSpec {

  describe(anInstanceOf[ArcgisFeatureToInternalRowEncoderImpl]) {
    describe(SHOULD) {
      describe("create an InternalRow from an ARCGIS feature") {
        it("including one value per field") {

          val fields = Seq(
            FeatureLayerField("id", EsriFieldType.INTEGER),
            FeatureLayerField("name", EsriFieldType.STRING)
          )

          val encoder = new ArcgisFeatureToInternalRowEncoderImpl(fields, None)
          val feature = ArcgisFeature()
        }
      }
    }
  }
}
