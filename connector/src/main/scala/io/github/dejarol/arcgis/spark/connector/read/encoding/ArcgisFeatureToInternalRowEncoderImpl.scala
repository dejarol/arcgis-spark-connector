package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.{UnsupportedArcgisGeometryTypeException, UnsupportedEsriFieldTypeException}
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriFieldType, EsriGeometryType, FeatureLayerField, PointGeometry, PolygonGeometry}
import io.github.dejarol.arcgis.spark.connector.read.models.ArcgisFeature
import org.apache.spark.sql.catalyst.InternalRow

/**
 * Encodes an ArcGIS feature into a Spark InternalRow using layer fields and geometry type.
 *
 * @param fields                feature layer attribute fields to encode
 * @param geometryType          ArcGIS geometry type of the layer
 * @param shouldIncludeGeometry whether the encoded row should include a geometry column
 * @since 0.1.0
 */
class ArcgisFeatureToInternalRowEncoderImpl(
                                          private val fields: Seq[FeatureLayerField],
                                          private val geometryType: EsriGeometryType,
                                          private val shouldIncludeGeometry: Boolean
                                          )
  extends ArcgisFeatureToInternalRowEncoder {

  /**
   * Encodes a feature's attributes and, when configured, its geometry into an InternalRow.
   *
   * @param feature ArcGIS feature to encode
   * @return an InternalRow whose columns follow the layer fields, plus geometry when included
   * @since 0.1.0
   */
  override def apply(feature: ArcgisFeature): InternalRow = {

    val attributeValues: Seq[Any] = fields.map {
      field => extractAttributeValueForField(feature, field)
    }

    val attributesAndGeometryValues: Seq[Any] = if (shouldIncludeGeometry) {
      attributeValues :+ extractGeometryValue(feature)
    } else attributeValues

    InternalRow(attributesAndGeometryValues: _*)
  }

  /**
   * Encodes a single feature attribute for a layer field.
   *
   * @param feature feature whose attributes are read
   * @param field   layer field that determines the attribute name and encoder
   * @return the Spark representation of the attribute
   * @throws UnsupportedEsriFieldTypeException if the field type has no attribute encoder
   * @throws java.lang.IllegalStateException   if the feature does not contain the field
   * @since 0.1.0
   */
  private def extractAttributeValueForField(
                                             feature: ArcgisFeature,
                                             field: FeatureLayerField
                                           ): Any = {

    feature.getAttribute(field.name) match {
      case Some(value) => parseAttributeValue(value, field.`type`)
      case None => throw new IllegalStateException(
        s"Feature does not contain attribute ${field.name}"
      )
    }
  }

  /**
   * Encodes an optional attribute value according to its ArcGIS field type.
   *
   * @param value  optional raw attribute value
   * @param `type` ArcGIS field type used to select the encoder
   * @return the Spark representation of the attribute
   * @throws UnsupportedEsriFieldTypeException if the field type has no attribute encoder
   * @since 0.1.0
   */
  private def parseAttributeValue(
                                   value: Option[Any],
                                   `type`: EsriFieldType
                                 ): Any = {

    `type` match {
      case EsriFieldType.DOUBLE => AttributeValueEncoders.forDouble().apply(value)
      case EsriFieldType.INTEGER | EsriFieldType.OID => AttributeValueEncoders.forInteger().apply(value)
      case EsriFieldType.STRING => AttributeValueEncoders.forString().apply(value)
      case _ => throw new UnsupportedEsriFieldTypeException(`type`)
    }
  }

  /**
   * Encodes the feature's geometry into an InternalRow.
   *
   * @param feature feature whose geometry is encoded
   * @return the encoded geometry InternalRow
   * @throws UnsupportedArcgisGeometryTypeException if the layer geometry type is unsupported or does not match the feature
   * @since 0.1.0
   */
  private def extractGeometryValue(feature: ArcgisFeature): InternalRow = {

    (geometryType, feature.geometry) match {
      case (EsriGeometryType.POINT, Some(p: PointGeometry)) => GeometryEncoders.forPoints().apply(p)
      case (EsriGeometryType.POLYGON, Some(p: PolygonGeometry)) => GeometryEncoders.forPolygons().apply(p)
      case _ => throw new UnsupportedArcgisGeometryTypeException(geometryType)
    }
  }
}
