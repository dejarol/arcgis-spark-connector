package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

class PartitioningConfigSpec
  extends BasicSpec {

  import PartitioningConfigSpec._

  private lazy val emptyConfig = createEmptyConfig()

  describe(anInstanceOf[PartitioningConfig]) {
    describe(SHOULD) {
      it("retrieve the maximum number of records per query") {

        // [1.1] Empty config
        emptyConfig.fetchSize shouldBe PartitioningConfig.FETCH_SIZE_DEFAULT

        // [1.2] Config with a custom value
        createConfig(
          Map(PartitioningConfig.FETCH_SIZE_KEY -> "5")
        ).fetchSize shouldBe 5
      }
    }
  }
}

object PartitioningConfigSpec {

  /**
   * TODO
   * @return
   */
  private def createEmptyConfig(): PartitioningConfig = {

    PartitioningConfig(
      CaseInsensitiveMap(
        Map.empty
      )
    )
  }

  /**
   * TODO
   * @param map
   * @return
   */
  private def createConfig(map: Map[String, String]): PartitioningConfig = {

    PartitioningConfig(
      CaseInsensitiveMap(map)
    )
  }
}
