package io.github.dejarol.arcgis.spark.connector.read.predicates

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.scalatest.OptionValues

class ArcgisPredicateTranslatorSpec
  extends BasicSpec
    with OptionValues {

  describe(`object`[ArcgisSQLPredicateBuilder.type ]) {
    describe(SHOULD) {
      describe("translate Spark predicates to SQL-92 predicates") {
        it("for checking null values") {

          // TODO
        }
      }
    }
  }
}
