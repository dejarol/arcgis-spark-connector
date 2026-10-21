package io.github.dejarol.arcgis.spark.connector.core.utils

/**
 * Helpers for wrapping strings with quotes and parentheses.
 *
 * @since 0.3.0
 */
object Strings {

  /**
   * Wraps a string in single quotes.
   *
   * @param s string to wrap
   * @return `s` enclosed in single quotes
   * @since 0.3.0
   */
  def withSingleQuote(s: String): String = f"'$s'"

  /**
   * Wraps a string in parentheses.
   *
   * @param s string to wrap
   * @return `s` enclosed in parentheses
   * @since 0.3.0
   */
  def withParenthesis(s: String): String = f"($s)"
}
