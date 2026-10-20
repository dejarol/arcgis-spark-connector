package io.github.dejarol.arcgis.spark.connector.core.utils

import java.time.temporal.ChronoUnit
import java.time.{Instant, OffsetDateTime, ZoneOffset}

/**
 * TODO
 */
object Time {

  /**
   * TODO
   * @param microseconds
   * @return
   */
  def microsecondsToUTCDate(microseconds: Long): OffsetDateTime = {

    Instant.EPOCH.plus(
      microseconds, ChronoUnit.MICROS
    ).atOffset(ZoneOffset.UTC)
  }
}
