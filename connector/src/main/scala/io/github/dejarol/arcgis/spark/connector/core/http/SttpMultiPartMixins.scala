package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.{BasicBodyPart, multipart}
import sttp.model.Part

import scala.collection.immutable.{Seq => ISeq}

/**
 * TODO
 */
trait SttpMultiPartMixins {

  /**
   * TODO
   * @param name
   * @param value
   * @return
   */
  protected final def createPart(name: String, value: String): Part[BasicBodyPart] = multipart(name, value)

  /**
   * TODO
   * @param parts
   * @return
   */
  protected final def createParts(parts: Map[String, String]): ISeq[Part[BasicBodyPart]] = {

    ISeq(
      parts.map {
      case (k, v) => createPart(k, v)
    }.toArray: _*
    )
  }
}
