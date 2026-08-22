package io.github.dejarol.arcgis.spark.connector.core.utils

import scala.reflect.ClassTag

/**
 * Lookup helpers for Java enums, including predicate-based and case-insensitive matching.
 *
 * @since 0.1.0
 */
object Enums {

  /**
   * Loads the runtime constants of a Java enum type.
   *
   * @tparam E Java enum type whose constants are loaded
   * @return the enum constants of `E`
   * @since 0.1.0
   */
  private def enumConstantsOf[E <: Enum[E] : ClassTag]: Array[E] = {

    implicitly[ClassTag[E]]
      .runtimeClass.asInstanceOf[Class[E]]
      .getEnumConstants
  }

  /**
   * Checks whether any constant of a Java enum matches the given value under a predicate.
   *
   * @param v         value compared against enum constants
   * @param predicate function that decides whether an enum constant matches `v`
   * @tparam E Java enum type whose constants are searched
   * @return `true` if at least one constant matches, `false` otherwise
   * @since 0.1.0
   */
  def exists[E <: Enum[E] : ClassTag](
                                       v: String,
                                       predicate: (E, String) => Boolean
                                     ): Boolean = {

    enumConstantsOf[E].exists {
      e => predicate(e, v)
    }
  }

  /**
   * Finds the first Java enum constant that matches the given value under a predicate.
   *
   * @param v         value compared against enum constants
   * @param predicate function that decides whether an enum constant matches `v`
   * @tparam E Java enum type whose constants are searched
   * @return the first matching constant, or `None` if none match
   * @since 0.1.0
   */
  def safeValueOfEnum[E <: Enum[E] : ClassTag](
                                                v: String,
                                                predicate: (E, String) => Boolean
                                              ): Option[E] = {

    enumConstantsOf[E].find {
      e => predicate(e, v)
    }
  }

  /**
   * Finds a Java enum constant whose name equals the given value, ignoring case.
   *
   * @param v value compared with each constant's `name()`
   * @tparam E Java enum type whose constants are searched
   * @return the matching constant, or `None` if none match ignoring case
   * @since 0.1.0
   */
  def caseInsensitiveSafeValueOfEnum[E <: Enum[E] : ClassTag](v: String): Option[E] = {

    safeValueOfEnum[E](
      v, (e, v) => e.name().equalsIgnoreCase(v)
    )
  }

  /**
   * Returns the first Java enum constant that matches the given value under a predicate.
   *
   * @param v         value compared against enum constants
   * @param predicate function that decides whether an enum constant matches `v`
   * @tparam E Java enum type whose constants are searched
   * @return the first matching constant
   * @throws NoSuchElementException if no constant matches `v`
   * @since 0.1.0
   */
  def unsafeValueOfEnum[E <: Enum[E] : ClassTag](
                                                  v: String,
                                                  predicate: (E, String) => Boolean
                                                ): E = {

    valueOrThrow(
      v, safeValueOfEnum[E](v, predicate)
    )
  }

  /**
   * Returns the Java enum constant whose name equals the given value, ignoring case.
   *
   * @param v value compared with each constant's `name()`
   * @tparam E Java enum type whose constants are searched
   * @return the matching constant
   * @throws NoSuchElementException if no constant matches `v` ignoring case
   * @since 0.1.0
   */
  def unsafeValueOfEnum[E <: Enum[E] : ClassTag](v: String): E = {

    valueOrThrow(
      v, caseInsensitiveSafeValueOfEnum[E](v)
    )
  }

  /**
   * Unwraps an optional enum constant, throwing if it is empty.
   *
   * @param v     value that was looked up
   * @param maybe optional matching enum constant
   * @tparam E Java enum type whose constants were searched
   * @return the matching constant
   * @throws NoSuchElementException if `maybe` is empty
   * @since 0.1.0
   */
  private def valueOrThrow[E <: Enum[E] : ClassTag](v: String, maybe: Option[E]): E = {

    maybe.getOrElse {
      throw new NoSuchElementException(
        s"No constant of ${implicitly[ClassTag[E]].runtimeClass.getName} matches value '$v'"
      )
    }
  }
}
