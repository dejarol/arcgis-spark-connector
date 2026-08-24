package io.github.dejarol.arcgis.spark.connector.core.config;

/**
 * TODO
 */
public class PropertyConversionException
        extends IllegalArgumentException {

    /**
     * TODO
     * @param message
     * @param cause
     */
    public PropertyConversionException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
