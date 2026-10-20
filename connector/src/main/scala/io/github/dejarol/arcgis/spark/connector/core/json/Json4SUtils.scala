package io.github.dejarol.arcgis.spark.connector.core.json

import org.json4s.JsonAST.JNumber
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats, JValue}

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
   * TODO
   * @param value
   * @param formats
   * @tparam E
   * @return
   */
  def asJSONString[E](
                       value: E,
                       formats: Formats = DefaultFormats
                     ): String = {

    Serialization.write(value)(formats)
  }
}
