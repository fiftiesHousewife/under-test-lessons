package io.github.fiftieshousewife.undertest.techniques.outgoing;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * Two partial refunds on one order are two refunds, so each has its own Idempotency-Key; a retry of the same refund
 * repeats its key, so the provider pays it once. A key made from the order's number would pay the second refund never.
 */
class RefundKeyTest {

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
    void eachRefundHasItsOwnKeyAndARetryRepeatsIt() {
        final Refunds refunds = Clients.at(provider.baseUrl());
        refunds.send(new Refund("RF-5501", "A-1001", 500));
        refunds.send(new Refund("RF-5502", "A-1001", 750));
        refunds.send(new Refund("RF-5502", "A-1001", 750));

        assertThat(provider.findAll(postRequestedFor(urlEqualTo("/refunds"))).stream()
                .map(RefundKeyTest::key).toList())
                .as("the Idempotency-Key of each refund sent")
                .isEqualTo(List.of("RF-5501", "RF-5502", "RF-5502"));
    }

    private static String key(final LoggedRequest request) {
        return request.containsHeader("Idempotency-Key") ? request.getHeader("Idempotency-Key") : "no key";
    }
}
