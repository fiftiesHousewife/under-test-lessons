package io.github.fiftieshousewife.undertest.techniques.recorded;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * BEFORE: a test against the supplier's real test service. It runs only where that service can be reached, with an
 * account, so it is skipped almost everywhere. And the real service only ever gives the happy answer on demand.
 */
class RealSupplierTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "POSTCODE_SUPPLIER_URL", matches = "https://.+")
    void knownPostcodeGivesItsTown() {
        assertThat(Clients.at(System.getenv("POSTCODE_SUPPLIER_URL")).town("LS1 4AP")).contains("Leeds");
    }
}
