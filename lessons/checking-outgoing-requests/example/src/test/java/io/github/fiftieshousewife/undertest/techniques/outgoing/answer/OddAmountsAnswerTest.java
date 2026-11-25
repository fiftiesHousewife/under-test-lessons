package io.github.fiftieshousewife.undertest.techniques.outgoing.answer;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import io.github.fiftieshousewife.undertest.techniques.outgoing.Clients;
import io.github.fiftieshousewife.undertest.techniques.outgoing.Refund;
import io.github.fiftieshousewife.undertest.techniques.outgoing.Refunds;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

/*
 * THE EXERCISE'S ANSWER: two more amounts, the smallest refund there is and one with odd pence, each read back from
 * the body of the request the client really sent. Both must go as whole pence. Run it against today's client with
 * {@code -Pcode=answer}.
 */
class OddAmountsAnswerTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @Test
    void oddAmountsAreSentInWholePence() {
        final WireMockServer provider = new WireMockServer(options().dynamicPort());
        provider.start();
        try {
            provider.stubFor(post(urlEqualTo("/refunds")).willReturn(okJson("{\"refundId\": \"R-7732\"}")
                    .withStatus(201)));
            final Refunds refunds = Clients.at(provider.baseUrl());
            refunds.send(new Refund("RF-5503", "A-1002", 1));
            refunds.send(new Refund("RF-5504", "A-1003", 1999));

            assertThat(provider.findAll(postRequestedFor(urlEqualTo("/refunds"))).stream()
                    .map(OddAmountsAnswerTest::amount).toList())
                    .as("the amount in each refund sent")
                    .isEqualTo(List.of("1", "1999"));
        } finally {
            provider.stop();
        }
    }

    private static String amount(final LoggedRequest request) {
        return JSON.readTree(request.getBodyAsString()).path("amount").asString();
    }
}
