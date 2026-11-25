package io.github.fiftieshousewife.undertest.techniques.faults.answer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import io.github.fiftieshousewife.undertest.techniques.faults.Clients;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: WireMock's fourth fault, which the strong tests leave out: the supplier sends random bytes
 * instead of an answer, then closes the connection. The shop must still say "supplier unavailable". Run it against
 * today's client with {@code -Pcode=answer}.
 */
class RandomDataAnswerTest {

    @Test
    void randomDataSaysTheSupplierIsUnavailable() {
        final WireMockServer supplier = new WireMockServer(options().dynamicPort());
        supplier.start();
        try {
            supplier.stubFor(get(urlEqualTo("/stock/TOASTER-4"))
                    .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));
            assertThat(Clients.outcome(Clients.at(supplier.baseUrl()), "TOASTER-4"))
                    .isEqualTo("supplier unavailable: the connection failed");
        } finally {
            supplier.stop();
        }
    }
}
