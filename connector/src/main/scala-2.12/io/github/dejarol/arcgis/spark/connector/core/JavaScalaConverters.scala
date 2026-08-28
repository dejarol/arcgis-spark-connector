package io.github.dejarol.arcgis.spark.connector.core

import scala.collection.JavaConverters._

/**
 * TODO
 */
object JavaScalaConverters {

  /**
   * TODO
   * @param javaMap
   * @tparam K
   * @tparam V
   * @return
   */
  def javaMapToScalaMap[K, V](javaMap: java.util.Map[K, V]): Map[K, V] = javaMap.asScala.toMap
}
