package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.{BasicBodyPart, multipart}
import sttp.model.Part

import scala.collection.immutable.{Seq => ISeq}

/**
 * Helpers for building STTP multipart form body parts from string fields.
 *
 * @since 0.1.0
 */
trait SttpMultiPartMixins {

  /**
   * Builds a single multipart form part from a name/value pair.
   *
   * @param name  form field name
   * @param value form field value
   * @return a multipart body part ready to attach to a request
   * @since 0.1.0
   */
  protected final def createPart(name: String, value: String): Part[BasicBodyPart] = multipart(name, value)

  /**
   * Builds multipart form parts from a map of field names to string values.
   *
   * @param parts field names mapped to their string values
   * @return an immutable sequence of multipart body parts
   * @since 0.1.0
   */
  protected final def createParts(parts: Map[String, String]): ISeq[Part[BasicBodyPart]] = {

    ISeq(
      parts.map {
      case (k, v) => createPart(k, v)
    }.toArray: _*
    )
  }
}
