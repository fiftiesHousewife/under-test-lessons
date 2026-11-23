package io.github.fiftieshousewife.undertest.techniques.unplugged.answer;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.fiftieshousewife.undertest.techniques.unplugged.CardPayments;
import io.github.fiftieshousewife.undertest.techniques.unplugged.QuoteApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * ANSWER: the exercise's stand-in for the payment gateway. The first test of CardPayments was refused, and the
 * agent's report named payments.prod.example.invalid, the test and the calling line; application.yml's
 * shop.payments.url holds it. Here a WireMock stand-in answers instead, and the test points the setting at it.
 */
@SpringBootTest(classes = QuoteApplication.class)
class CardPaymentsAnswerTest {

    private static final WireMockServer GATEWAY = startGateway();

    @Autowired
    private CardPayments payments;

    private static WireMockServer startGateway() {
        final WireMockServer gateway = new WireMockServer(wireMockConfig().dynamicPort());
        gateway.start();
        return gateway;
    }

    @DynamicPropertySource
    static void gatewaySettings(final DynamicPropertyRegistry settings) {
        settings.add("shop.payments.url", GATEWAY::baseUrl);
    }

    @BeforeEach
    void gatewayAnswers() {
        GATEWAY.resetAll();
        GATEWAY.stubFor(post("/payments").willReturn(okJson("{\"reference\": \"PAY-7781\"}")));
    }

    @AfterAll
    static void stopGateway() {
        GATEWAY.stop();
    }

    @Test
    void keepsTheGatewaysReference() {
        assertThat(payments.take("Q-1001", 2894)).as("the payment's reference").isEqualTo("PAY-7781");
    }

    @Test
    void sendsTheOrderAndTheAmountInPence() {
        payments.take("Q-1001", 2894);
        GATEWAY.verify(postRequestedFor(urlEqualTo("/payments"))
                .withRequestBody(equalToJson("{\"order\": \"Q-1001\", \"pence\": 2894}")));
    }
}
