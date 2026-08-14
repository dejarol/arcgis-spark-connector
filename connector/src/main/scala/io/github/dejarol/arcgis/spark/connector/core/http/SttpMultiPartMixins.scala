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
   * Builds multipart form parts from a map of field names to string values.
   *
   * @param parts field names mapped to their string values
   * @return an immutable sequence of multipart body parts
   * @since 0.1.0
   */
  protected final def createParts(parts: Map[String, String]): ISeq[Part[BasicBodyPart]] = {

    ISeq(
      parts.map {
      case (k, v) => multipart(k, v)
    }.toArray: _*
    )
  }
}
