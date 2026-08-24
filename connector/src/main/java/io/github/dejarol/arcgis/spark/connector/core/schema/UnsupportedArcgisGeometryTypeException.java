package io.github.dejarol.arcgis.spark.connector.core.schema;

import io.github.dejarol.arcgis.spark.connector.core.models.EsriGeometryType;
import org.jetbrains.annotations.NotNull;

/**
 * Indicates that an ArcGIS geometry type cannot be mapped to a Spark SQL type.
 *
 * @since 0.1.0
 */
public class UnsupportedArcgisGeometryTypeException
        extends IllegalArgumentException {

    /**
     * Creates an exception for an unsupported ArcGIS geometry type.
     *
     * @param geometryType ArcGIS geometry type that has no Spark mapping
     * @since 0.1.0
     */
    public UnsupportedArcgisGeometryTypeException(
            @NotNull EsriGeometryType geometryType
    ) {
        super("Unsupported ArcGIS geometry type: " + geometryType);
    }
}
