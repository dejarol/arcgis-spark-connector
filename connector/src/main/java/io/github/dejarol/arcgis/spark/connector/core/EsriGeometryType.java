package io.github.dejarol.arcgis.spark.connector.core;

import org.jetbrains.annotations.Contract;

/**
 * TODO
 */
public enum EsriGeometryType
        implements EnumWithAPIName {

    POLYGON("esriGeometryPolygon"),
    POINT("esriGeometryPoint");

    private final String apiName;

    /**
     * TODO
     * @param apiName
     */
    @Contract(pure = true)
    EsriGeometryType(String apiName) {
        this.apiName = apiName;
    }

    /**
     * TODO
     * @return
     */
    @Override
    @Contract(pure = true)
    public String getAPIName() {
        return apiName;
    }
}
