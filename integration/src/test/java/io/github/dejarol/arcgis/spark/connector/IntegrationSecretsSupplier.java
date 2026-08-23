package io.github.dejarol.arcgis.spark.connector;

/**
 * Supplies ArcGIS credentials used by integration tests.
 *
 * @since 0.1.0
 */
public interface IntegrationSecretsSupplier {

    /**
     * Returns the ArcGIS deployment root URL.
     *
     * @return the root URL
     * @since 0.1.0
     */
    String root();

    /**
     * Returns the ArcGIS account user name.
     *
     * @return the user name
     * @since 0.1.0
     */
    String username();

    /**
     * Returns the ArcGIS account password.
     *
     * @return the password
     * @since 0.1.0
     */
    String password();
}
