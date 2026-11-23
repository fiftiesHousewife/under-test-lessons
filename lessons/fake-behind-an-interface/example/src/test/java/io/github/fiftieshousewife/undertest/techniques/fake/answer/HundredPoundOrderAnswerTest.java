package io.github.fiftieshousewife.undertest.techniques.fake.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.fake.InMemoryMessageQueue;
import io.github.fiftieshousewife.undertest.techniques.fake.OrderPlacement;
import io.github.fiftieshousewife.undertest.techniques.fake.Placements;
import org.junit.jupiter.api.Test;

/**
 * THE EXERCISE'S ANSWER: the boundary. An order of exactly £100 is the smallest that needs a fraud check, so its
 * fraud-check message must be delivered. Run it against today's order placement with {@code -Pcode=answer}.
 */
class HundredPoundOrderAnswerTest {

    @Test
    void orderOfExactlyOneHundredPoundsReachesTheFraudCheck() {
        final InMemoryMessageQueue queue = new InMemoryMessageQueue();
        Placements.using(queue).place("A-3", OrderPlacement.FRAUD_CHECK_PENCE);

        assertThat(queue.delivered("fraud-checks")).as("fraud checks").containsExactly("A-3 10000");
    }
}
