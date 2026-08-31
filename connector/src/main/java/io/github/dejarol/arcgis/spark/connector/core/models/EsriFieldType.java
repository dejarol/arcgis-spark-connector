package io.github.dejarol.arcgis.spark.connector.core.models;

import io.github.dejarol.arcgis.spark.connector.core.EnumWithAPIName;
import org.jetbrains.annotations.Contract;

/**
 * ArcGIS field data types used in feature layer definitions.
 *
 * @since 0.1.0
 */
public enum EsriFieldType
        implements EnumWithAPIName {

    DATE("esriFieldTypeDate"),
    DOUBLE("esriFieldTypeDouble"),
    INTEGER("esriFieldTypeInteger"),
    OID("esriFieldTypeOID"),
    SMALL_INTEGER("esriFieldTypeSmallInteger"),
    STRING("esriFieldTypeString");

    private final String apiName;

    /**
     * Associates this constant with its ArcGIS API name.
     *
     * @param apiName name used by the ArcGIS REST API
     * @since 0.1.0
     */
    @Contract(pure = true)
    EsriFieldType(String apiName) {
        this.apiName = apiName;
    }

    /**
     * Returns the ArcGIS REST API name of this field type.
     *
     * @return the API name
     * @since 0.1.0
     */
    @Contract(pure = true)
    @Override
    public String getAPIName() {
        return apiName;
    }
}
