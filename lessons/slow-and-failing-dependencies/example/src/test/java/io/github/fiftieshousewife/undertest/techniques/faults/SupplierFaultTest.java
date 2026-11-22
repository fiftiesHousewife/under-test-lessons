package io.github.fiftieshousewife.undertest.techniques.faults;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * AFTER: WireMock makes the supplier fail on demand: an answer five seconds late, a connection reset, an empty answer,
 * a broken one. Each test checks the shop answers within 3 seconds, with a clear "supplier unavailable".
 */
class SupplierFaultTest {

    private static final Duration THREE_SECONDS = Duration.ofSeconds(3);
    private static final String WAITED_TOO_LONG = "the shop kept the customer waiting for the supplier";

    private final WireMockServer supplier = new WireMockServer(options().dynamicPort());

    @BeforeEach
    void start() {
        supplier.start();
    }

    @AfterEach
    void stop() {
        supplier.stop();
    }

    @Test
    void slowSupplierCostsTheShopOneTimeout() {
        supplier.stubFor(get(urlEqualTo("/stock/KETTLE-2")).willReturn(okJson("{\"units\": 12}").withFixedDelay(5000)));
        final String outcome = assertTimeoutPreemptively(THREE_SECONDS, this::askForKettles, WAITED_TOO_LONG);
        assertThat(outcome).isEqualTo("supplier unavailable: no answer within 500 ms");
    }

    @ParameterizedTest
    @EnumSource(value = Fault.class, names = {"CONNECTION_RESET_BY_PEER", "EMPTY_RESPONSE", "MALFORMED_RESPONSE_CHUNK"})
    void brokenConnectionSaysTheSupplierIsUnavailable(final Fault fault) {
        supplier.stubFor(get(urlEqualTo("/stock/KETTLE-2")).willReturn(aResponse().withFault(fault)));
        final String outcome = assertTimeoutPreemptively(THREE_SECONDS, this::askForKettles, WAITED_TOO_LONG);
        assertThat(outcome).as(fault.name()).isEqualTo("supplier unavailable: the connection failed");
    }

    private String askForKettles() {
        return Clients.outcome(Clients.at(supplier.baseUrl()), "KETTLE-2");
    }
}
