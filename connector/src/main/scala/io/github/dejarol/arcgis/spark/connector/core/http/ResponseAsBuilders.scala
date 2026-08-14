package io.github.dejarol.arcgis.spark.connector.core.http

import org.json4s.native.JsonMethods.{parseOpt, pretty, render}
import org.json4s.{DefaultFormats, Formats, JValue}
import sttp.client4.{ResponseAs, asStringAlways}

/**
 * Factory methods for [[ResponseAsBuilder]] instances used by STTP requests.
 *
 * @since 0.1.0
 */
object ResponseAsBuilders {

  /**
   * Parses the response body as JSON and extracts a value of type `R`.
   *
   * Do not instantiate directly; use [[ResponseAsBuilders.eitherThrowableOr]] instead.
   *
   * @param formats JSON formats used for extraction
   * @tparam R expected response payload type
   * @since 0.1.0
   */
  private case class EitherThrowableOr[R: Manifest](private val formats: Formats)
    extends ResponseAsBuilder[Either[Throwable, R]] {

    /**
     * Builds a response decoder that yields `Either[Throwable, R]`.
     *
     * @return decoder that fails with [[ResponseNotJsonException]] or [[ResponseFromAPIException]] on the left
     * @since 0.1.0
     */
    override def build(): ResponseAs[Either[Throwable, R]] = {

      asStringAlways.map {
        // At first, try to parse the response body as JSON
        s => parseOpt(s).toRight {
          new ResponseNotJsonException(s)
        }.flatMap(extractValue)
      }
    }

    /**
     * Extracts a value of type `R` from a parsed JSON tree.
     *
     * @param jValue parsed JSON value
     * @return the extracted value, or a [[ResponseFromAPIException]] on the left
     * @since 0.1.0
     */
    private def extractValue(jValue: JValue): Either[Throwable, R] = {

      // Safely convert the parsed JSON tree to a value of type `R`.
      // If the conversion fails, create a dedicated exception.
      jValue.extractOpt[R](
        formats, implicitly[Manifest[R]]
      ).toRight {
        new ResponseFromAPIException(
          pretty(render(jValue))
        )
      }
    }
  }

  /**
   * Creates a builder that decodes JSON responses into `Either[Throwable, R]`.
   *
   * @tparam R expected response payload type
   * @return a response-as builder for `Either[Throwable, R]`
   * @since 0.1.0
   */
  def eitherThrowableOr[R: Manifest](): ResponseAsBuilder[Either[Throwable, R]] = {

    EitherThrowableOr[R](DefaultFormats)
  }
}
