package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}

class PartitioningConfigSpec
  extends BasicSpec
    with JavaMapMixins {

  private lazy val emptyConfig = PartitioningConfig(createEmptyMap())

  describe(anInstanceOf[PartitioningConfig]) {
    describe(SHOULD) {
      it("retrieve the maximum number of records per query") {

        // [1.1] Empty config
        emptyConfig.fetchSize shouldBe PartitioningConfig.FETCH_SIZE_DEFAULT

        // [1.2] Config with a custom value
        PartitioningConfig(
          createSingletonMap(PartitioningConfig.FETCH_SIZE_KEY, "5")
        ).fetchSize shouldBe 5
      }
    }
  }
}
