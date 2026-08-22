package io.github.dejarol.arcgis.spark.connector;

/**
 * TODO
 */
public interface IntegrationSecretsSupplier {

    /**
     * TODO
     * @return
     */
    String root();

    /**
     * TODO
     * @return
     */
    String username();

    /**
     * TODO
     * @return
     */
    String password();
}
