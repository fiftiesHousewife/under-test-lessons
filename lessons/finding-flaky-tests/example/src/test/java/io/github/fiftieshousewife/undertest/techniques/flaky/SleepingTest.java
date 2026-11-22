package io.github.fiftieshousewife.undertest.techniques.flaky;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

/** BEFORE: places an order, sleeps a fixed 200 ms, then checks. It guesses how long the confirmation will take. */
class SleepingTest {

    private static final MailServer MAIL = MailServers.chosen();

    @DisplayName("the confirmation is sent")
    @RepeatedTest(value = 5, name = "run {currentRepetition} of {totalRepetitions}")
    void confirmsTheOrder(final RepetitionInfo run) throws InterruptedException {
        final int order = run.getCurrentRepetition();
        new OrderDesk(MAIL).placeOrder(order);

        Thread.sleep(200);

        assertThat(MAIL.sent()).withFailMessage("no confirmation of order %d after 200 ms", order)
                .contains("Order " + order + " confirmed");
    }
}
