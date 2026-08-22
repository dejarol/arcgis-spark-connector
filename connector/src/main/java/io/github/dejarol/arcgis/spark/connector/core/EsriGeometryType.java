package io.github.dejarol.arcgis.spark.connector.core;

public enum EsriGeometryType {

    POLYGON("esriGeometryPolygon"),
    POINT("esriGeometryPoint");

    private final String apiName;

    EsriGeometryType(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }
}
