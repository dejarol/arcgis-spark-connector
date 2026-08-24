package io.github.dejarol.arcgis.spark.connector.core;

import org.apache.spark.sql.util.CaseInsensitiveStringMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 */
public final class JavaCollectionsUtils {

    /**
     * TODO
     * @param map
     * @param prefix
     * @param <V>
     * @return
     */
    public static <V> Map<String, V> filterMapByPrefix(
            @NotNull Map<String, V> map,
            String prefix
    ) {

        return map.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .map(s -> new AbstractMap.SimpleEntry<>(
                        s.getKey().substring(prefix.length()), s.getValue()
                        )
                ).collect(
                        Collectors.toMap(
                                AbstractMap.SimpleEntry::getKey,
                                AbstractMap.SimpleEntry::getValue
                        )
                );
    }

    /**
     * TODO
     * @param first
     * @param second
     * @return
     */
    @Contract("_, _ -> new")
    public static @NotNull CaseInsensitiveStringMap mergeCaseInsensitiveMaps(
            @NotNull CaseInsensitiveStringMap first,
            @NotNull CaseInsensitiveStringMap second
    ) {

        Map<String, String> original = new HashMap<>();
        original.putAll(first.asCaseSensitiveMap());
        original.putAll(second.asCaseSensitiveMap());
        return new CaseInsensitiveStringMap(original);
    }
}
