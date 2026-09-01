package io.github.dejarol.arcgis.spark.connector.core

import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

/**
 * Base suite for tests that build Spark case-insensitive configuration maps.
 *
 * Provides helpers for empty, singleton, and general [[CaseInsensitiveMap]] instances.
 *
 * @since 0.1.0
 */
trait ConfigSpec
  extends BasicSpec {

  protected final val EMPTY_CIMAP: CaseInsensitiveMap[String] = CaseInsensitiveMap(
    Map.empty[String, String]
  )

  /**
   * Creates a case-insensitive map with a single entry.
   *
   * @param k property name
   * @param v property value
   * @return a map containing only `k` -> `v`
   * @since 0.1.0
   */
  protected final def createSingletonCIMap(
                                            k: String,
                                            v: String
                                          ): CaseInsensitiveMap[String] = {

    CaseInsensitiveMap(
      Map(k -> v)
    )
  }

  /**
   * Creates a case-insensitive map from the given entries.
   *
   * @param map entries keyed by property name
   * @return a case-insensitive copy of `map`
   * @since 0.1.0
   */
  protected final def createCIMap(map: Map[String, String]): CaseInsensitiveMap[String] = {

    CaseInsensitiveMap(map)
  }
}
