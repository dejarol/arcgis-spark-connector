package io.github.dejarol.arcgis.spark.connector.core.config;

import org.jetbrains.annotations.NotNull;

/**
 * Indicates that a configuration property value could not be converted to the expected type.
 *
 * @since 0.1.0
 */
public class PropertyConversionException
        extends IllegalArgumentException {

    /**
     * Creates an exception describing a failed property conversion.
     *
     * @param key        property name that failed conversion
     * @param conversion conversion that was applied
     * @param cause      error raised while converting the value
     * @since 0.1.0
     */
    public PropertyConversionException(
            String key,
            @NotNull PropertyConversion<?> conversion,
            Throwable cause
    ) {
        super(
                String.format(
                        "Failed to convert property value for key '%s' to %s",
                        key, conversion.targetTypeDescription()
                ), cause);
    }
}
