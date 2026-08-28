package io.github.dejarol.arcgis.spark.connector.core.http

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.scalatest.enablers.ValueMapping
import sttp.client4.{BasicBodyPart, StringBody}
import sttp.model.Part

/**
 * Base suite for tests of types that encode themselves as STTP multipart parts.
 *
 * Provides a helper to convert parts to a map keyed by part name.
 *
 * @since 0.1.0
 */
trait AsMultiPartSpec
  extends BasicSpec {

  /**
   * Converts an [[AsMultiParts]] instance into a map of parts keyed by name.
   *
   * @param partsImpl instance whose parts are converted
   * @return a map from part name to part
   * @since 0.1.0
   */
  protected final def convertPartsToMap(partsImpl: AsMultiParts): Map[String, Part[BasicBodyPart]] = {

    partsImpl.parts().map{
      part => (part.name, part)
    }.toMap
  }
}

object AsMultiPartSpec {

  /**
   * ScalaTest [[ValueMapping]] for STTP multipart parts with a string body.
   *
   * @since 0.1.0
   */
  implicit object BasicBodyPartValueMapping
    extends ValueMapping[Part[BasicBodyPart]] {

    /**
     * Reports whether the part body equals the expected value.
     *
     * @param map   multipart part whose body is compared
     * @param value expected body value
     * @return `true` if the part body matches `value`
     * @since 0.1.0
     */
    override def containsValue(map: Part[BasicBodyPart], value: Any): Boolean = {

      (map.body, value) match {
        case (StringBody(actual, _, _), expected: String) => actual.equals(expected)
        case (StringBody(actual, _, _), expected: Int) => actual.equals(String.valueOf(expected))
        case (StringBody(actual, _, _), expected: Boolean) => actual.equals(String.valueOf(expected.toString))
        case (StringBody(actual, _, _), expected: Seq[String]) => actual.equals(expected.mkString(","))
        case _ => false
      }
    }
  }
}
