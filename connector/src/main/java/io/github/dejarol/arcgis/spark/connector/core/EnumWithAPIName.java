package io.github.dejarol.arcgis.spark.connector.core;

/**
 * Marks an enum constant that is identified by a name in the ArcGIS REST API.
 *
 * @since 0.1.0
 */
public interface EnumWithAPIName {

    /**
     * Returns the name used by the ArcGIS REST API.
     *
     * @return the API name
     * @since 0.1.0
     */
    String getAPIName();

    /**
     * Tests whether the given name matches this value's API name, ignoring case.
     *
     * @param name candidate API name
     * @return {@code true} if {@code name} equals this value's API name, ignoring case
     * @since 0.1.0
     */
    default boolean matchesAPIName(String name) {

        return getAPIName().equalsIgnoreCase(name);
    }
}
