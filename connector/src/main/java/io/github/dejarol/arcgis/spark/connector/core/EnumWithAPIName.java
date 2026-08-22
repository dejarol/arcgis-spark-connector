package io.github.dejarol.arcgis.spark.connector.core;

/**
 * TODO
 */
public interface EnumWithAPIName {

    /**
     * TODO
     * @return
     */
    String getAPIName();

    /**
     * TODO
     * @param name
     * @return
     */
    default boolean matchesAPIName(String name) {

        return getAPIName().equalsIgnoreCase(name);
    }
}
