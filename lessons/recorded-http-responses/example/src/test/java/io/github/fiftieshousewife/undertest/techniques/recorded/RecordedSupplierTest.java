package io.github.fiftieshousewife.undertest.techniques.recorded;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * AFTER: WireMock replays the supplier's recorded answers (src/test/resources/supplier/mappings) on a local port,
 * including the ones the real service rarely gives: an unknown postcode, and an outage.
 */
class RecordedSupplierTest {

    private final WireMockServer supplier = new WireMockServer(options().dynamicPort()
            .usingFilesUnderClasspath("supplier"));

    @BeforeEach
    void start() {
        supplier.start();
    }

    @AfterEach
    void stop() {
        supplier.stop();
    }

    @Test
    void knownPostcodeGivesItsTown() {
        assertThat(Clients.at(supplier.baseUrl()).town("LS1 4AP")).contains("Leeds");
    }

    @Test
    void unknownPostcodeGivesNoTown() {
        assertThat(Clients.at(supplier.baseUrl()).town("ZZ9 9ZZ")).isEmpty();
    }

    @Test
    void outageSaysTheSupplierIsUnavailable() {
        final Throwable error = catchThrowable(() -> Clients.at(supplier.baseUrl()).town("M1 1AE"));
        assertThat(describe(error)).isEqualTo("SupplierUnavailableException: the postcode supplier answered 503");
    }

    @Test
    void unreachableSupplierSaysSo() {
        final String address = supplier.baseUrl();
        supplier.stop();
        final Throwable error = catchThrowable(() -> Clients.at(address).town("LS1 4AP"));
        assertThat(describe(error)).isEqualTo("SupplierUnavailableException: the postcode supplier can't be reached");
    }

    /* The error's type and the first line of its message, so a failure reads in one line. */
    private static String describe(final Throwable error) {
        return error == null ? "no error"
                : error.getClass().getSimpleName() + ": " + error.getMessage().lines().findFirst().orElse("");
    }
}
