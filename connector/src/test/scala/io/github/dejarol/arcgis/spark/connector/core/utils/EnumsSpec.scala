package io.github.dejarol.arcgis.spark.connector.core.utils

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.models.EsriGeometryType

class EnumsSpec
  extends BasicSpec {

  import EnumsSpec._

  describe(`object`[Enums.type]) {
    describe(SHOULD) {
      it("evaluate whether an enum constant matches a predicate") {

        Enums.exists[EsriGeometryType](
          EsriGeometryType.POLYGON.name(),
          matchesName
        ) shouldBe true

        Enums.exists[EsriGeometryType](
          "unknown",
          matchesName
        ) shouldBe false
      }

      it("return the first enum constant that matches a predicate") {

        Enums.safeValueOfEnum[EsriGeometryType](
          EsriGeometryType.POINT.getAPIName,
          matchesApiName
        ) shouldBe Some(EsriGeometryType.POINT)

        Enums.safeValueOfEnum[EsriGeometryType](
          "unknown",
          matchesApiName
        ) shouldBe empty
      }

      it("return an enum constant whose name matches ignoring case") {

        Enums.caseInsensitiveSafeValueOfEnum[EsriGeometryType]("polygon") shouldBe Some(EsriGeometryType.POLYGON)
        Enums.caseInsensitiveSafeValueOfEnum[EsriGeometryType]("POINT") shouldBe Some(EsriGeometryType.POINT)
        Enums.caseInsensitiveSafeValueOfEnum[EsriGeometryType]("unknown") shouldBe empty
      }

      it("return the first enum constant that matches a predicate or throw if none match") {

        Enums.unsafeValueOfEnum[EsriGeometryType](
          EsriGeometryType.POINT.getAPIName,
          matchesApiName
        ) shouldBe EsriGeometryType.POINT

        a [NoSuchElementException] shouldBe thrownBy {
          Enums.unsafeValueOfEnum[EsriGeometryType]("unknown", matchesApiName)
        }
      }

      it("return an enum constant whose name matches ignoring case or throw if none match") {

        Enums.unsafeValueOfEnum[EsriGeometryType]("polygon") shouldBe EsriGeometryType.POLYGON
        Enums.unsafeValueOfEnum[EsriGeometryType]("POINT") shouldBe EsriGeometryType.POINT

        a [NoSuchElementException] shouldBe thrownBy {
          Enums.unsafeValueOfEnum[EsriGeometryType]("unknown")
        }
      }
    }
  }
}

object EnumsSpec {

  private lazy val matchesName: (EsriGeometryType, String) => Boolean = (v, s) => v.name().equals(s)
  private lazy val matchesApiName: (EsriGeometryType, String) => Boolean = (v, s) => v.getAPIName.equals(s)
}
