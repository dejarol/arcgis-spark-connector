package io.github.dejarol.arcgis.spark.connector.core

class JavaCollectionsUtilsSpec
  extends BasicSpec
    with JavaMapMixins {

  describe(anInstanceOf[JavaCollectionsUtils]) {
    describe(SHOULD) {
      it("filter a map keeping only entries starting with a given prefix") {

        val map = createSimpleMap(
          ("query.where", "v1"),
          ("k2", "v2")
        )

        val filtered = JavaCollectionsUtils.filterMapByPrefix(map, "query.")
        filtered should contain key "where"
        filtered.get("where") shouldBe "v1"
        filtered shouldNot contain key "k2"
      }

      describe("merge 2 case-insensitive maps") {
        it("with different keys") {

          val first = createCIMap("k1", "v1")
          val second = createCIMap("k2", "v2")
          val merged = JavaCollectionsUtils.mergeCaseInsensitiveMaps(first, second)
          merged should contain key "k1"
          merged should contain key "k2"
        }

        it("with same keys") {

          val first = createCIMap("k", "v1")
          val second = createCIMap("k", "v2")
          val merged = JavaCollectionsUtils.mergeCaseInsensitiveMaps(first, second)
          merged should contain key "k"
          merged.get("k") shouldBe "v2"
        }
      }
    }
  }
}
