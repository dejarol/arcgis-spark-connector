package io.github.dejarol.arcgis.spark.connector.core.schema

import io.github.dejarol.arcgis.spark.connector.core.{UnsupportedArcgisGeometryTypeException, UnsupportedEsriFieldTypeException}
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriFieldType, EsriGeometryType, FeatureLayerField}
import org.apache.spark.sql.types._

/**
 * Converts ArcGIS feature layer definitions into Spark SQL schemas.
 *
 * @since 0.1.0
 */
object SchemaUtils {

  /**
   * Name of the Spark column that stores feature geometry.
   *
   * @since 0.1.0
   */
  final val GEOMETRY_COLUMN_NAME = "geometry"

  /**
   * Builds a Spark schema from feature layer fields, optionally appending a geometry column.
   *
   * @param fields   feature layer attribute fields to convert
   * @param geometry optional ArcGIS geometry type; when present, a geometry column is appended
   * @return a Spark schema for the feature layer
   * @since 0.1.0
   */
  def toStructType(
                    fields: Seq[FeatureLayerField],
                    geometry: Option[EsriGeometryType]
                  ): StructType = {

    // First, convert all attributes fields to StructFields. Then, add geometry column if required
    val featureLayerDefaultFields = fields.map(featureLayerFieldToStructField)
    val featureLayerAndGeometryFields = geometry.map {
      g => featureLayerDefaultFields :+ geometryField(g)
    }.getOrElse(featureLayerDefaultFields)

    StructType(featureLayerAndGeometryFields)
  }

  /**
   * Converts an ArcGIS feature layer field into a Spark struct field.
   *
   * @param field feature layer field to convert
   * @return a Spark struct field with the mapped data type
   * @throws UnsupportedEsriFieldTypeException if the field type has no Spark mapping
   * @since 0.1.0
   */
  protected[schema] def featureLayerFieldToStructField(field: FeatureLayerField): StructField = {

    val sparkType: DataType = field.`type` match {
      case EsriFieldType.DATE => TimestampType
      case EsriFieldType.DOUBLE => DoubleType
      case EsriFieldType.INTEGER | EsriFieldType.OID | EsriFieldType.SMALL_INTEGER => IntegerType
      case EsriFieldType.STRING => StringType
      case _ => throw new UnsupportedEsriFieldTypeException(field.`type`)
    }

    StructField(field.name, sparkType)
  }

  /**
   * Builds the Spark struct field that holds feature geometry.
   *
   * @param geometryType ArcGIS geometry type of the layer
   * @return a struct field named [[GEOMETRY_COLUMN_NAME]] with the matching Spark geometry type
   * @throws UnsupportedArcgisGeometryTypeException if the geometry type has no Spark mapping
   * @since 0.1.0
   */
  protected[schema] def geometryField(geometryType: EsriGeometryType): StructField = {

    val sparkGeometryType: DataType = geometryType match {
      case EsriGeometryType.POLYGON => SparkGeometryTypes.POLYGON
      case EsriGeometryType.POINT => SparkGeometryTypes.POINT
      case _ => throw new UnsupportedArcgisGeometryTypeException(geometryType)
    }

    StructField(GEOMETRY_COLUMN_NAME, sparkGeometryType)
  }
}
