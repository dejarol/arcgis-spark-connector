package io.github.dejarol.arcgis.spark.connector.core

import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.util

/**
 * TODO
 */
trait JavaMapMixins {

  /**
   * TODO
   * @return
   */

  protected final def createEmptyMap(): util.Map[String, String] = {
    util.Collections.emptyMap()
  }

  /**
   * TODO
   * @param k
   * @param v
   * @return
   */
  protected final def createCIMap(
                                   k: String,
                                   v: String
                                 ): CaseInsensitiveStringMap = {

    new CaseInsensitiveStringMap(
      util.Collections.singletonMap(k, v)
    )
  }

  /**
   * TODO
   * @param k
   * @param v
   * @return
   */
  protected final def createSimpleMap(
                                       k: String,
                                       v: String
                                     ): util.Map[String, String] = {

    util.Collections.singletonMap(k, v)
  }

  /**
   * @param first
   * @param others
   * @return
   */
  protected final def createSimpleMap(
                                       first: (String, String),
                                       others: (String, String)*
                                     ): util.Map[String, String] = {

    val (k, v) = first
    val javaMap = new util.HashMap[String, String]()
    javaMap.put(k, v)
    others.foreach {
      case (k, v) => javaMap.put(k, v)
    }
    javaMap
  }
}
