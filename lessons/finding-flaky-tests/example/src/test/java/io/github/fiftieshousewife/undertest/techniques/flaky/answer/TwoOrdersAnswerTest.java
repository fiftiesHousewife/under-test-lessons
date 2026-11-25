package io.github.fiftieshousewife.undertest.techniques.flaky.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.github.fiftieshousewife.undertest.techniques.flaky.MailServer;
import io.github.fiftieshousewife.undertest.techniques.flaky.MailServers;
import io.github.fiftieshousewife.undertest.techniques.flaky.OrderDesk;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

/*
 * The exercise's answer: two orders at once, and a wait for both confirmations. Awaitility retries the AssertJ check
 * until it passes, so if time runs out the failure names the confirmations that were missing.
 */
class TwoOrdersAnswerTest {

    private static final MailServer MAIL = MailServers.chosen();

    @DisplayName("both confirmations are sent")
    @RepeatedTest(value = 5, name = "run {currentRepetition} of {totalRepetitions}")
    void confirmsBothOrders(final RepetitionInfo run) {
        final OrderDesk desk = new OrderDesk(MAIL);
        final int first = 100 + run.getCurrentRepetition();
        final int second = 200 + run.getCurrentRepetition();
        desk.placeOrder(first);
        desk.placeOrder(second);

        await().atMost(Duration.ofSeconds(2)).pollInterval(Duration.ofMillis(50)).untilAsserted(() ->
                assertThat(MAIL.sent()).contains("Order " + first + " confirmed", "Order " + second + " confirmed"));
    }
}
