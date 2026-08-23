package io.github.dejarol.arcgis.spark.connector.core.http;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import scala.reflect.Manifest;

/**
 * Indicates that a parsed ArcGIS API response could not be mapped to the expected type.
 *
 * @since 0.1.0
 */
public class MismatchingAPIResponseException
        extends RuntimeException {

    /**
     * Creates an exception describing a mapping failure.
     *
     * @param message description of the mapping failure and received response
     * @param cause   error raised while mapping the response
     * @since 0.1.0
     */
    public MismatchingAPIResponseException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }

    /**
     * Creates an exception describing a response that could not be mapped to the expected type.
     *
     * @param apiResponse raw API response that failed to map
     * @param manifest    expected response type
     * @param cause       error raised while mapping the response
     * @param <T>         expected response type
     * @return an exception containing the expected type and formatted response
     * @since 0.1.0
     */
    @Contract("_, _, _ -> new")
    public static <T> @NotNull MismatchingAPIResponseException create(
            @NotNull String apiResponse,
            @NotNull Manifest<T> manifest,
            @NotNull Throwable cause
    ) {

        return new MismatchingAPIResponseException(
            "Received an API response that could not be mapped to " + manifest.toString() + ". " +
            "Response content was\n" + apiResponse,
            cause
        );
    }
}
