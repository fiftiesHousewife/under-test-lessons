package io.github.fiftieshousewife.undertest.techniques.outgoing;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * AFTER: WireMock keeps every request it receives, and verify checks the one the code really sent: its method,
 * address, headers, and its JSON body compared field by field.
 */
class RefundRequestTest {

    private final WireMockServer provider = new WireMockServer(options().dynamicPort());

    @BeforeEach
    void start() {
        provider.start();
        provider.stubFor(post(urlEqualTo("/refunds")).willReturn(okJson("{\"refundId\": \"R-7731\"}").withStatus(201)));
    }

    @AfterEach
    void stop() {
        provider.stop();
    }

    @Test
    void sendsTheRefundTheProviderExpects() {
        Clients.at(provider.baseUrl()).send(new Refund("A-1001", 1250));
        provider.verify(postRequestedFor(urlEqualTo("/refunds"))
                .withHeader("Authorization", equalTo("Bearer " + Clients.TEST_KEY))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Idempotency-Key", equalTo("refund-A-1001"))
                .withRequestBody(equalToJson("""
                        {"orderId": "A-1001", "amount": 1250, "currency": "GBP"}""")));
    }
}
