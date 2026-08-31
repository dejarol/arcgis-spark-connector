package io.github.dejarol.arcgis.spark.connector.core

import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

/**
 * TODO
 */
trait ConfigSpec
  extends BasicSpec {

  protected final val EMPTY_CIMAP: CaseInsensitiveMap[String] = CaseInsensitiveMap(
    Map.empty[String, String]
  )

  /**
   * TODO
   * @param k
   * @param v
   * @return
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
   * TODO
   * @param map
   * @return
   */
  protected final def createCIMap(map: Map[String, String]): CaseInsensitiveMap[String] = {

    CaseInsensitiveMap(map)
  }
}
