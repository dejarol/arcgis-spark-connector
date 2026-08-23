package io.github.dejarol.arcgis.spark.connector.core.http

import org.json4s.JValue
import org.json4s.native.JsonMethods.{pretty, render}

/**
 * Indicates that a parsed ArcGIS API response could not be mapped to the expected type.
 *
 * Do not instantiate directly; use [[MismatchingAPIResponseException.create]] instead.
 *
 * @param message description of the mapping failure and received response
 * @param cause   error raised while mapping the response
 * @since 0.1.0
 */
case class MismatchingAPIResponseException private(
                                                    message: String,
                                                    cause: Throwable
                                                  )
  extends RuntimeException(message, cause)

object MismatchingAPIResponseException {

  /**
   * Creates an exception describing a response that could not be mapped to the expected type.
   *
   * @param jValue parsed API response that failed to map
   * @param cause  error raised while mapping the response
   * @tparam A expected response type
   * @return an exception containing the expected type and formatted response
   * @since 0.1.0
   */
  def create[A: Manifest](
                           jValue: JValue,
                           cause: Throwable
                         ): MismatchingAPIResponseException = {

    val valueDescription = implicitly[Manifest[A]].toString()
    MismatchingAPIResponseException(
      f"Received an API response that could not be mapped to $valueDescription. " +
        f"Response content was\n${pretty(render(jValue))}",
      cause
    )
  }
}

