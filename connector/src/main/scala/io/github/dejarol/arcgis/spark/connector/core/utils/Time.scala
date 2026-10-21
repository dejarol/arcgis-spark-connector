package io.github.dejarol.arcgis.spark.connector.core.utils

import java.time.temporal.ChronoUnit
import java.time.{Instant, OffsetDateTime, ZoneOffset}

/**
 * Conversions between epoch-based time values and UTC dates.
 *
 * @since 0.3.0
 */
object Time {

  /**
   * Converts a microsecond offset from the Unix epoch to a UTC date-time.
   *
   * @param microseconds microseconds since the Unix epoch
   * @return the corresponding UTC offset date-time
   * @since 0.3.0
   */
  def microsecondsToUTCDate(microseconds: Long): OffsetDateTime = {

    Instant.EPOCH.plus(
      microseconds, ChronoUnit.MICROS
    ).atOffset(ZoneOffset.UTC)
  }
}
