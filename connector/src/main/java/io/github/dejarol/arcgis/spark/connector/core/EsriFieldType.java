package io.github.dejarol.arcgis.spark.connector.core;

import org.jetbrains.annotations.Contract;

public enum EsriFieldType
        implements EnumWithAPIName {

    STRING("esriFieldTypeString");

    private final String apiName;

    /**
     * @param apiName
     */
    @Contract(pure = true)
    EsriFieldType(String apiName) {
        this.apiName = apiName;
    }

    /**
     * @return
     */
    @Contract(pure = true)
    @Override
    public String getAPIName() {
        return apiName;
    }
}
