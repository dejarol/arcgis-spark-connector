package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.models.{EsriFieldType, EsriGeometryType, FeatureLayerField}
import io.github.dejarol.arcgis.spark.connector.core.{UnsupportedArcgisGeometryTypeException, UnsupportedEsriFieldTypeException}
import io.github.dejarol.arcgis.spark.connector.read.models.ArcgisFeature
import org.apache.spark.sql.catalyst.InternalRow
import org.json4s.JsonAST.JValue

/**
 * Encodes an ArcGIS feature into a Spark InternalRow using layer fields and geometry type.
 *
 * @param fields                feature layer attribute fields to encode
 * @param maybeGeometryType          ArcGIS geometry type of the layer
 * @since 0.1.0
 */
class ArcgisFeatureToInternalRowEncoderImpl(
                                             private val fields: Seq[FeatureLayerField],
                                             private val maybeGeometryType: Option[EsriGeometryType]
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
      field => extractAttributeValue(
        feature.unsafelyGetAttribute(field.name),
        field.`type`
      )
    }

    val attributesAndGeometryValues: Seq[Any] = maybeGeometryType.map {
      geometryType => attributeValues :+ extractGeometryValue(feature, geometryType)
    }.getOrElse(attributeValues)

    InternalRow(attributesAndGeometryValues: _*)
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
  private def extractAttributeValue(
                                     value: JValue,
                                     `type`: EsriFieldType
                                   ): Any = {

    `type` match {
      case EsriFieldType.DATE => AttributeValueEncoders.forDate().apply(value)
      case EsriFieldType.DOUBLE => AttributeValueEncoders.forDouble().apply(value)
      case EsriFieldType.INTEGER | EsriFieldType.OID | EsriFieldType.SMALL_INTEGER => AttributeValueEncoders.forInteger().apply(value)
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
  private def extractGeometryValue(
                                    feature: ArcgisFeature,
                                    geometryType: EsriGeometryType
                                  ): InternalRow = {

    // [1] If geometry value exists, process it
    feature.geometry match {
      case Some(value) =>

        // [1.1] Process geometry value based on geometry type
        geometryType match {
          case EsriGeometryType.POINT => GeometryEncoders.forPoints().apply(value)
          case EsriGeometryType.POLYGON => GeometryEncoders.forPolygons().apply(value)
          case _ => throw new UnsupportedArcgisGeometryTypeException(geometryType)
        }

      // [2] If geometry value does not exist, throw an exception
      case None => throw new IllegalStateException("Geometry value not found in this feature")
    }
  }
}
