package io.github.dejarol.arcgis.spark.connector.core.json

import io.github.dejarol.arcgis.spark.connector.core.EnumWithAPIName

/**
 * Simple model for validating JSON4S customizations.
 * @param `type` the type
 * @tparam E type value (should be an enum that extends [[EnumWithAPIName]])
 */
case class ModelWithEnumWithAPIName[E <: Enum[E] with EnumWithAPIName](`type`: E)
