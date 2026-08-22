package io.github.dejarol.arcgis.spark.connector.core;

public enum EsriFieldType {

    STRING("esriFieldTypeString");

    private final String apiName;

    EsriFieldType(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }
}
