package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}
import sttp.model.Uri

class ReadConfigSpec
  extends BasicSpec {

  import ReadConfigSpec._

  private lazy val emptyReadConfig: ReadConfig = createEmptyReadConfig()

  describe(anInstanceOf[ReadConfig]) {
    describe(SHOULD) {
      it("retrieve the layerUri") {

        emptyReadConfig.layerUri shouldBe empty

        val value = "http://localhost:6080/arcgis/rest/services/ServiceName/MapServer/0"
        val valid = createReadConfig(ReadConfig.LAYER_URI_KEY, value)
        valid.layerUri shouldBe Some(Uri.unsafeParse(value))
      }
    }
  }
}

object ReadConfigSpec
  extends JavaMapMixins {

  /**
   * TODO
   * @return
   */
  private def createEmptyReadConfig(): ReadConfig = {

    ReadConfig(
      createEmptyMap()
    )
  }

  /**
   * TODO
   * @param k
   * @param v
   * @return
   */
  private def createReadConfig(
                              k: String,
                              v: String
                              ): ReadConfig = {

    ReadConfig(
      createCIMap(k, v)
    )
  }
}