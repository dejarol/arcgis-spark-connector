package io.github.dejarol.arcgis.spark.connector.core.json

import org.json4s.JsonAST.JNumber
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats, JValue}

/**
 * Helpers for inspecting json4s values and serializing Scala objects to JSON.
 *
 * @since 0.1.0
 */
object Json4SUtils {

  /**
   * Returns whether the JSON value is a numeric node.
   *
   * @param value JSON value to inspect
   * @return `true` when the value is an integer, long, decimal, or double
   * @since 0.1.0
   */
  def isNumber(value: JValue): Boolean = {

    value match {
      case _: JNumber => true
      case _ => false
    }
  }

  /**
   * Serializes a value to a JSON string using json4s.
   *
   * @param value   value to serialize
   * @param formats json4s formats used during serialization
   * @tparam E type of the value to serialize
   * @return a JSON string representation of `value`
   * @since 0.3.0
   */
  def asJSONString[E](
                       value: E,
                       formats: Formats = DefaultFormats
                     ): String = {

    Serialization.write(value)(formats)
  }
}
