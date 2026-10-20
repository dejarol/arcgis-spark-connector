package io.github.dejarol.arcgis.spark.connector.core.utils

/**
 * TODO
 */
object Strings {

  /**
   * TODO
   * @param s
   * @return
   */
  def withSingleQuote(s: String): String = f"'$s'"

  /**
   * TODO
   * @param s
   * @return
   */
  def withParenthesis(s: String): String = f"($s)"
}
