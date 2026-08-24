package io.github.dejarol.arcgis.spark.connector.core.config;

/**
 * TODO
 * @param <T>
 */
public interface PropertyConversion<T> {

    /**
     * TODO
     * @param value
     * @return
     */
    T apply(String value);

    /**
     * TODO
     * @return
     */
    String targetTypeDescription();
}
