package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.apache.spark.unsafe.types.UTF8String
import org.json4s.JLong
import org.json4s.JsonAST.{JDecimal, JDouble, JInt, JNull, JString}

class AttributeValueEncodersSpec
  extends BasicSpec {

  describe(`object`[AttributeValueEncoders.type ]) {
    describe(SHOULD) {
      describe("provide mappers for") {
        it("date") {

          val mapper = AttributeValueEncoders.forDate()
          mapper(JNull) shouldBe null
          mapper(JLong(1)) shouldBe 1000
          mapper(JInt(BigInt(1))) shouldBe 1000
          mapper(JDecimal(1)) shouldBe 1000

          an [IllegalArgumentException] shouldBe thrownBy {
            mapper(JString("hello"))
          }
        }

        it("double") {

          val mapper = AttributeValueEncoders.forDouble()
          mapper(JNull) shouldBe null
          mapper(JDouble(1.23)) shouldBe 1.23

          an [IllegalArgumentException] shouldBe thrownBy {
            mapper(JString("1.23"))
          }
        }

        it("integer") {

          val mapper = AttributeValueEncoders.forInteger()
          mapper(JNull) shouldBe null
          mapper(JInt(BigInt(1))) shouldBe 1

          an [IllegalArgumentException] shouldBe thrownBy {
            mapper(JString("hello"))
          }
        }

        it("string") {

          val mapper = AttributeValueEncoders.forString()
          mapper(JNull) shouldBe null
          mapper(JString("hello")) shouldBe a[UTF8String]

          an [IllegalArgumentException] shouldBe thrownBy {
            mapper(JDouble(3.14))
          }
        }
      }
    }
  }
}
