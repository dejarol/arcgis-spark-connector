package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.apache.spark.unsafe.types.UTF8String

class AttributeValueEncodersSpec
  extends BasicSpec {

  describe(`object`[AttributeValueEncoders.type ]) {
    describe(SHOULD) {
      describe("provide mappers for") {
        it("double") {

          val mapper = AttributeValueEncoders.forDouble()
          mapper(None) shouldBe null
          mapper(Some(1.23)) shouldBe 1.23

          an [IllegalArgumentException] shouldBe thrownBy {
            mapper(Some("1.23"))
          }
        }

        it("integer") {

          val mapper = AttributeValueEncoders.forInteger()
          mapper(None) shouldBe null
          mapper(Some(1)) shouldBe 1

          an [IllegalArgumentException] shouldBe thrownBy {
            mapper(Some("hello"))
          }
        }

        it("string") {

          val mapper = AttributeValueEncoders.forString()
          mapper(None) shouldBe null
          mapper(Some("hello")) shouldBe a[UTF8String]

          an [IllegalArgumentException] shouldBe thrownBy {
            mapper(Some(1))
          }
        }
      }
    }
  }
}
