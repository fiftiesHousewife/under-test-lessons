package io.github.fiftieshousewife.undertest.techniques.recorded.today;

import io.github.fiftieshousewife.undertest.techniques.recorded.PostcodeLookup;
import io.github.fiftieshousewife.undertest.techniques.recorded.SupplierHttp;
import java.util.Optional;

/** TODAY: reads the town from every answer, whatever the supplier said. Written against the happy path only. */
public final class TodaysPostcodeClient implements PostcodeLookup {

    private final SupplierHttp supplier;

    public TodaysPostcodeClient(final SupplierHttp supplier) {
        this.supplier = supplier;
    }

    @Override
    public Optional<String> town(final String postcode) {
        return Optional.of(SupplierHttp.townIn(supplier.get(postcode).body()));
    }
}
