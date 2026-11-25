package io.github.fiftieshousewife.undertest.techniques.faults;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * BEFORE: a stand-in supplier answers at once, with stock or with an error status. The tests check both are handled,
 * and never try a supplier that answers late, drops the connection or sends a broken answer.
 */
class StockAnswerTest {

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
    void supplierWithStockGivesTheUnits() {
        supplier.stubFor(get(urlEqualTo("/stock/KETTLE-2")).willReturn(okJson("{\"units\": 12}")));
        assertThat(Clients.outcome(Clients.at(supplier.baseUrl()), "KETTLE-2")).isEqualTo("12 in stock");
    }

    @Test
    void supplierOutageSaysTheSupplierIsUnavailable() {
        supplier.stubFor(get(urlEqualTo("/stock/KETTLE-2")).willReturn(aResponse().withStatus(503)));
        assertThat(Clients.outcome(Clients.at(supplier.baseUrl()), "KETTLE-2"))
                .isEqualTo("supplier unavailable: the supplier answered 503");
    }
}
