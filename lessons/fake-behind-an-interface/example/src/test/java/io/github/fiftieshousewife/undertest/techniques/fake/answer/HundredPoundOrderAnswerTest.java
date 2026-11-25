package io.github.fiftieshousewife.undertest.techniques.fake.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.fake.InMemoryMessageQueue;
import io.github.fiftieshousewife.undertest.techniques.fake.OrderPlacement;
import io.github.fiftieshousewife.undertest.techniques.fake.Placements;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: the boundary. An order of exactly £100 is the smallest that needs a fraud check, so a
 * fraud-check message must be sent for it. The fake keeps every message sent, delivered or not, so this test tells
 * a check never sent from one sent and lost, which FakeQueueTest already finds. Run it against today's order
 * placement with {@code -Pcode=answer}.
 */
class HundredPoundOrderAnswerTest {

    @Test
    void orderOfExactlyOneHundredPoundsSendsAFraudCheck() {
        final InMemoryMessageQueue queue = new InMemoryMessageQueue();
        Placements.using(queue).place("A-3", OrderPlacement.FRAUD_CHECK_PENCE);

        assertThat(queue.sent("fraud-checks")).as("fraud checks sent, delivered or not").containsExactly("A-3 10000");
    }
}
