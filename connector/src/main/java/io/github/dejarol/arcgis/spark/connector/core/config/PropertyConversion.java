package io.github.dejarol.arcgis.spark.connector.core.config;

/**
 * TODO
 * @param <T>
 */
@FunctionalInterface
public interface PropertyConversion<T> {

    /**
     * TODO
     * @param value
     * @return
     */
    T apply(String value);
}
