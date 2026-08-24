package io.github.dejarol.arcgis.spark.connector.core.schema

import io.github.dejarol.arcgis.spark.connector.core.EsriGeometryType
import io.github.dejarol.arcgis.spark.connector.core.http.item.FeatureLayerField
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

object SchemaUtils {

  final val POINT_GEOMETRY_TYPE: StructType = StructType(
    Seq(
      StructField("x", DoubleType, nullable = true),
      StructField("y", DoubleType, nullable = true)
    )
  )

  def toStructType(
                    fields: Seq[FeatureLayerField],
                    geometry: Option[EsriGeometryType]
                  ): StructType = {

    val featureLayerDefaultFields = fields.map(featureLayerFieldToStructField)
    val featureLayerAndGeometryFields = geometry.map {
      g => maybeAddGeometryField(featureLayerDefaultFields, g)
    }.getOrElse(featureLayerDefaultFields)

    StructType(featureLayerAndGeometryFields)
  }

  def featureLayerFieldToStructField(field: FeatureLayerField): StructField = {

    null
  }

  def maybeAddGeometryField(
                             original: Seq[StructField],
                             geometryType: EsriGeometryType
                           ): Seq[StructField] = {

    original
  }
}
