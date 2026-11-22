package io.github.fiftieshousewife.undertest.techniques.slices;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * AFTER: one test of the whole application, kept only for the wiring: the web layer, the service, the database and
 * the courier's client, joined by Spring, answer one request together. The slices check each layer's details.
 */
class DeliveriesWiringTest extends WholeApplication {

    @Test
    void answersWithEveryPartJoinedUp() {
        assertThat(bodyOf("/customers/ann/deliveries")).as("Ann's deliveries")
                .contains("\"reference\":\"D-1002\"", "\"tracking\":\"Out for delivery\"");
    }
}
