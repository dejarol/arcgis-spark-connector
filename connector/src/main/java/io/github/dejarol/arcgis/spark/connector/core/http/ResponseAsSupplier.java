package io.github.dejarol.arcgis.spark.connector.core.http;

import sttp.client4.ResponseAs;

import java.util.function.Supplier;

@FunctionalInterface
public interface ResponseAsSupplier<A>
        extends Supplier<ResponseAs<A>> {
}
