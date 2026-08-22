package io.github.dejarol.arcgis.spark.connector.core.http;

import org.jetbrains.annotations.NotNull;

/**
 * Exception raised when an ArcGIS REST API JSON payload cannot be mapped to the expected type.
 *
 * @since 0.1.0
 */
public class UnexpectedAPIResponse
        extends RuntimeException {

    /**
     * Creates an exception that wraps the API response text.
     *
     * @param apiMessage raw API response (or message) that could not be interpreted as success
     * @since 0.1.0
     */
    public UnexpectedAPIResponse(
            @NotNull String apiMessage
    ) {
        super("Unexpected ARCGIS API response: " + apiMessage);
    }
}
