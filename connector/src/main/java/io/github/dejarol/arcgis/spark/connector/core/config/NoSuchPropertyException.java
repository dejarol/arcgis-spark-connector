package io.github.dejarol.arcgis.spark.connector.core.config;

/**
 * Indicates that a required configuration property is missing.
 *
 * @since 0.1.0
 */
public class NoSuchPropertyException
        extends IllegalArgumentException {

    /**
     * Creates an exception for a missing required property.
     *
     * @param key missing property name
     * @since 0.1.0
     */
    public NoSuchPropertyException(String key) {
        super("Missing required property: " + key);
    }
}
