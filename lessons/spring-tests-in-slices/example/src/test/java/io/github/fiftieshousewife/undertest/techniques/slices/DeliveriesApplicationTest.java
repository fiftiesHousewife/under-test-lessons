package io.github.fiftieshousewife.undertest.techniques.slices;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * BEFORE: every test starts the whole application, so each needs the database, the courier's stand-in and every
 * setting. They pass, but none of them looks at how the due date is written.
 */
class DeliveriesApplicationTest extends WholeApplication {

    @Test
    void listsTheCustomersDeliveries() {
        assertThat(bodyOf("/customers/ann/deliveries")).as("Ann's deliveries").contains("D-1001", "D-1002");
    }

    @Test
    void leavesOutWhatHasBeenDelivered() {
        assertThat(bodyOf("/customers/ann/deliveries")).as("Ann's deliveries").doesNotContain("D-1000");
    }

    @Test
    void showsWhatTheCourierSays() {
        assertThat(bodyOf("/customers/ann/deliveries")).as("Ann's deliveries").contains("At the depot");
    }
}
