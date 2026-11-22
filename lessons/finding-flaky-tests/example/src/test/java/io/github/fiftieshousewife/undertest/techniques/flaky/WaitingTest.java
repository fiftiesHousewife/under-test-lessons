package io.github.fiftieshousewife.undertest.techniques.flaky;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

/** AFTER: waits until the confirmation has gone, checking every 50 ms for up to 2 seconds, and goes on at once. */
class WaitingTest {

    private static final MailServer MAIL = MailServers.chosen();

    @DisplayName("the confirmation is sent")
    @RepeatedTest(value = 5, name = "run {currentRepetition} of {totalRepetitions}")
    void confirmsTheOrder(final RepetitionInfo run) {
        final int order = run.getCurrentRepetition();
        new OrderDesk(MAIL).placeOrder(order);

        await("the confirmation of order " + order)
                .atMost(Duration.ofSeconds(2))
                .pollInterval(Duration.ofMillis(50))
                .until(() -> MAIL.sent().contains("Order " + order + " confirmed"));
    }
}
