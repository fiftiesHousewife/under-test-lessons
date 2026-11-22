package io.github.fiftieshousewife.undertest.techniques.outgoing;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * BEFORE: a stand-in provider gives its answer, and the tests check the code handled it. Nothing checks the request
 * the code sent, so a wrong amount or a missing header goes unnoticed.
 */
class RefundAnswerTest {

    private final WireMockServer provider = new WireMockServer(options().dynamicPort());

    @BeforeEach
    void start() {
        provider.start();
    }

    @AfterEach
    void stop() {
        provider.stop();
    }

    @Test
    void acceptedRefundGivesItsId() {
        provider.stubFor(post(urlEqualTo("/refunds")).willReturn(okJson("{\"refundId\": \"R-7731\"}").withStatus(201)));
        assertThat(Clients.at(provider.baseUrl()).send(Clients.REFUND)).isEqualTo("R-7731");
    }

    @Test
    void outageAndSilenceSayTheProviderIsUnavailable() {
        provider.stubFor(post(urlEqualTo("/refunds")).willReturn(aResponse().withStatus(503)));
        final Throwable outage = catchThrowable(() -> Clients.at(provider.baseUrl()).send(Clients.REFUND));
        final String address = provider.baseUrl();
        provider.stop();
        final Throwable silence = catchThrowable(() -> Clients.at(address).send(Clients.REFUND));
        assertAll(
                () -> assertThat(describe(outage)).as("an outage")
                        .isEqualTo("ProviderUnavailableException: the payment provider answered 503"),
                () -> assertThat(describe(silence)).as("no answer at all")
                        .isEqualTo("ProviderUnavailableException: the payment provider can't be reached"));
    }

    /** The error's type and message, so a failure reads in one line. */
    private static String describe(final Throwable error) {
        return error == null ? "no error" : error.getClass().getSimpleName() + ": " + error.getMessage();
    }
}
