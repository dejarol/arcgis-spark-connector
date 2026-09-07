package io.github.dejarol.arcgis.spark.connector.core.config;

import org.jetbrains.annotations.NotNull;

/**
 * TODO
 * @param <T>
 */
public interface ExtendableConfig<T> {

    /**
     * TODO
     * @param key
     * @param value
     * @return
     */
    T withOption(
            @NotNull String key,
            @NotNull String value
    );
}
