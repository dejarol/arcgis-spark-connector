package io.github.dejarol.arcgis.spark.connector.core

import scala.collection.JavaConverters._

/**
 * Converts Java collection types to their Scala equivalents.
 *
 * @since 0.1.0
 */
object JavaScalaConverters {

  /**
   * Converts a Java map to an immutable Scala map.
   *
   * @param javaMap Java map to convert
   * @tparam K key type
   * @tparam V value type
   * @return an immutable Scala map with the same entries
   * @since 0.1.0
   */
  def javaMapToScalaMap[K, V](javaMap: java.util.Map[K, V]): Map[K, V] = javaMap.asScala.toMap
}
