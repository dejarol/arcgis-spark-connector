package io.github.dejarol.arcgis.spark.connector.core

import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, Formats, JObject}

/**
 * Mixin trait for JSON conversion utilities.
 *
 * @since 0.1.0
 */
trait JSONMixins {

  /**
   * Converts a JSON string into a value in the JSON4s AST hierarchy.
   *
   * @param json a JSON string
   * @return a value in the JSON4s AST hierarchy
   * @since 0.1.0
   */
  protected final def asJValue(json: String): JValue = JsonMethods.parse(json)

  /**
   * Parses a JSON string into a value of type `T`.
   *
   * @param rawJson JSON document to parse
   * @param formats JSON4s formats used for extraction
   * @tparam T expected value type
   * @return the extracted value
   * @since 0.1.0
   */
  protected final def jsonStringAS[T: Manifest](rawJson: String, formats: Formats): T = {

    asJValue(rawJson).extract[T](formats, manifest[T])
  }

  protected final def caseClassToJObject[T <: Product](value: T): JObject = {

    Extraction.decompose(value)(DefaultFormats) match {
      case obj: JObject => obj
      case _ => throw new IllegalArgumentException(
        f"Provided argument is not a case class"
      )
    }
  }
}
