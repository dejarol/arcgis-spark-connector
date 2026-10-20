package io.github.dejarol.arcgis.spark.connector.core.utils

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec

class StringsSpec
  extends BasicSpec {

  describe(`object`[Strings.type ]) {
    describe(SHOULD) {
      it("manipulate strings") {

        Strings.withSingleQuote("hello") shouldBe "'hello'"
        Strings.withParenthesis("hello") shouldBe "(hello)"
      }
    }
  }
}
