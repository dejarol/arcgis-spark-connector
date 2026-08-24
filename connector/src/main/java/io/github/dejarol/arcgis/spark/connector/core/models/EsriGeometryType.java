package io.github.dejarol.arcgis.spark.connector.core.models;

import io.github.dejarol.arcgis.spark.connector.core.EnumWithAPIName;
import org.jetbrains.annotations.Contract;

/**
 * ArcGIS geometry types used in feature layer definitions.
 *
 * @since 0.1.0
 */
public enum EsriGeometryType
        implements EnumWithAPIName {

    POLYGON("esriGeometryPolygon"),
    POINT("esriGeometryPoint");

    private final String apiName;

    /**
     * Associates this constant with its ArcGIS API name.
     *
     * @param apiName name used by the ArcGIS REST API
     * @since 0.1.0
     */
    @Contract(pure = true)
    EsriGeometryType(String apiName) {
        this.apiName = apiName;
    }

    /**
     * Returns the ArcGIS REST API name of this geometry type.
     *
     * @return the API name
     * @since 0.1.0
     */
    @Override
    @Contract(pure = true)
    public String getAPIName() {
        return apiName;
    }
}
