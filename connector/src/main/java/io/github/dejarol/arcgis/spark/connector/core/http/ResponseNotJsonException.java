package io.github.dejarol.arcgis.spark.connector.core.http;

import org.jetbrains.annotations.NotNull;

/**
 * Exception raised when an HTTP response body is not valid JSON.
 *
 * @since 0.1.0
 */
public class ResponseNotJsonException
        extends IllegalArgumentException {

    /**
     * Creates an exception that includes the non-JSON response text.
     *
     * @param responseText raw response body that failed JSON parsing
     * @since 0.1.0
     */
    public ResponseNotJsonException(
            @NotNull String responseText
    ) {

        super("ARCGIS API's response is not JSON: " + responseText);
    }
}
