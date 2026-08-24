package io.github.dejarol.arcgis.spark.connector.core.config;

import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public class PropertyConversionException
        extends IllegalArgumentException {

    /**
     * TODO
     * @param key
     * @param conversion
     * @param cause
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
