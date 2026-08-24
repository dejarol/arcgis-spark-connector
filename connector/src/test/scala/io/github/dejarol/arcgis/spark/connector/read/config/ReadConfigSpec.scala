package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.BaseConfig
import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}
import org.scalatest.enablers.KeyMapping
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

      it("retrieve query options") {

        emptyReadConfig.queryLayerConfig shouldBe empty

        val queryConfig = createReadConfig(
          (ReadConfig.QUERY_PREFIX + "k1", "v1"),
          ("k2", "v2")
        ).queryLayerConfig

        queryConfig shouldNot be (empty)
        queryConfig should contain key "k1"
        queryConfig shouldNot contain key "k2"
      }
    }
  }
}

object ReadConfigSpec
  extends JavaMapMixins {

  // Implicit value for allowing the use of the `contain` matcher on BaseConfig
  lazy implicit val BASE_CONFIG_KEY_MAPPING: KeyMapping[BaseConfig] =
    (map: BaseConfig, key: Any) => map.containsKey(String.valueOf(key))

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

  /**
   * @param first
   * @param others
   * @return
   */
  private def createReadConfig(
                                first: (String, String),
                                others: (String, String)*
                              ): ReadConfig = {

    ReadConfig(
      createSimpleMap(first, others: _*)
    )
  }
}