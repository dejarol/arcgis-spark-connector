package io.github.dejarol.arcgis.spark.connector.core.utils

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec

import java.time.{LocalDate, LocalTime, OffsetDateTime, ZoneOffset}

class TimeSpec
  extends BasicSpec {

  describe(`object`[Time.type ]) {
    describe(SHOULD) {
      it("convert microseconds to a UTC date") {

        val dt = Time.microsecondsToUTCDate(1704067200000000L)
        dt shouldBe OffsetDateTime.of(
          LocalDate.of(2024, 1, 1),
          LocalTime.MIDNIGHT,
          ZoneOffset.UTC
        )
      }
    }
  }
}
