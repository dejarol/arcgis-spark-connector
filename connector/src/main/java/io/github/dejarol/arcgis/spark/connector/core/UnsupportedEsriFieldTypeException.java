package io.github.dejarol.arcgis.spark.connector.core;

import io.github.dejarol.arcgis.spark.connector.core.models.EsriFieldType;
import org.jetbrains.annotations.NotNull;

/**
 * Indicates that an ArcGIS field data type cannot be mapped to a Spark SQL type.
 *
 * @since 0.1.0
 */
public class UnsupportedEsriFieldTypeException
        extends IllegalArgumentException {

    /**
     * Creates an exception for an unsupported ArcGIS field type.
     *
     * @param fieldType ArcGIS field type that has no Spark mapping
     * @since 0.1.0
     */
    public UnsupportedEsriFieldTypeException(
            @NotNull EsriFieldType fieldType
    ) {
        super("Unsupported ArcGIS data type: " + fieldType.getAPIName());
    }
}
