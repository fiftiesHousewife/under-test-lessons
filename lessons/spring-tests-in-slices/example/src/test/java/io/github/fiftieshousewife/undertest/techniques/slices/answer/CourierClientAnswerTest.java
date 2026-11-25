package io.github.fiftieshousewife.undertest.techniques.slices.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import io.github.fiftieshousewife.undertest.techniques.slices.CourierClient;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/*
 * THE EXERCISE'S ANSWER: the courier's client is the one layer with no test of its own, and no test reaches its "Not
 * known" answer. Spring's MockRestServiceServer stands in for the courier behind the client's RestClient, so only
 * the client runs: no application, no WireMock, no database.
 */
class CourierClientAnswerTest {

    @Test
    void saysNotKnownWhenTheCourierAnswersWithNothing() {
        final RestClient.Builder builder = RestClient.builder();
        final MockRestServiceServer courier = MockRestServiceServer.bindTo(builder).build();
        courier.expect(requestTo("https://courier.test/tracking/D-1001")).andRespond(withSuccess());

        final String tracking = new CourierClient(builder, "https://courier.test").trackingFor("D-1001");

        assertAll(
                courier::verify,
                () -> assertThat(tracking).as("what the courier says about D-1001").isEqualTo("Not known"));
    }
}
