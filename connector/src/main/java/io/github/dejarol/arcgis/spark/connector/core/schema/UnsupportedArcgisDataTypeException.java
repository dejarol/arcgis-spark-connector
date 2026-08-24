package io.github.dejarol.arcgis.spark.connector.core.schema;

import io.github.dejarol.arcgis.spark.connector.core.models.EsriFieldType;
import org.jetbrains.annotations.NotNull;

/**
 * Indicates that an ArcGIS field data type cannot be mapped to a Spark SQL type.
 *
 * @since 0.1.0
 */
public class UnsupportedArcgisDataTypeException
        extends IllegalArgumentException {

    /**
     * Creates an exception for an unsupported ArcGIS field type.
     *
     * @param fieldType ArcGIS field type that has no Spark mapping
     * @since 0.1.0
     */
    public UnsupportedArcgisDataTypeException(
            @NotNull EsriFieldType fieldType
    ) {
        super("Unsupported ArcGIS data type: " + fieldType.getAPIName());
    }
}
