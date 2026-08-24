package io.github.dejarol.arcgis.spark.connector.core.config;

/**
 * TODO
 */
public class NoSuchPropertyException
        extends IllegalArgumentException {

    /**
     * TODO
     * @param key
     */
    public NoSuchPropertyException(String key) {
        super("Missing required property: " + key);
    }
}
