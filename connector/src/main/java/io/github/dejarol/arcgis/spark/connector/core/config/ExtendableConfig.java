package io.github.dejarol.arcgis.spark.connector.core.config;

import org.jetbrains.annotations.NotNull;

/**
 * Configuration that can be copied with an additional key/value option.
 *
 * @param <T> concrete configuration type returned by {@link #withOption(String, String)}
 * @since 0.1.0
 */
public interface ExtendableConfig<T> {

    /**
     * Returns a copy of this configuration with the given option set.
     *
     * @param key   option name
     * @param value option value
     * @return a new configuration of type {@code T} that includes {@code key}
     * @since 0.1.0
     */
    T withOption(
            @NotNull String key,
            @NotNull String value
    );
}
