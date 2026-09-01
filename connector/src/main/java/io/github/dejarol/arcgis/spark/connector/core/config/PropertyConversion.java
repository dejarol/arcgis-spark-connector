package io.github.dejarol.arcgis.spark.connector.core.config;

/**
 * Converts a raw configuration property string to a typed value.
 *
 * @param <T> converted value type
 * @since 0.1.0
 */
public interface PropertyConversion<T> {

    /**
     * Converts a raw property string to type {@code T}.
     *
     * @param value raw property string
     * @return the converted value
     * @since 0.1.0
     */
    T apply(String value);

    /**
     * Returns a short description of the target type, used in conversion-error messages.
     *
     * @return a human-readable target type name
     * @since 0.1.0
     */
    String targetTypeDescription();
}
