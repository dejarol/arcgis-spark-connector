package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec

class PartitioningConfigSpec
  extends ConfigSpec {

  private lazy val emptyConfig = PartitioningConfig(EMPTY_CIMAP)

  describe(anInstanceOf[PartitioningConfig]) {
    describe(SHOULD) {
      it("retrieve the maximum number of records per query") {

        // [1.1] Empty config
        emptyConfig.fetchSize shouldBe PartitioningConfig.FETCH_SIZE_DEFAULT

        // [1.2] Config with a custom value
        PartitioningConfig(
          createCIMap(
            Map(PartitioningConfig.FETCH_SIZE_KEY -> "5")
          )
        ).fetchSize shouldBe 5
      }
    }
  }
}
