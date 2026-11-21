package io.github.fiftieshousewife.undertest.techniques.fake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

/** AFTER: a fake queue that behaves like the real one. The test checks what was delivered, not the calls made. */
class FakeQueueTest {

    private final InMemoryMessageQueue queue = new InMemoryMessageQueue();

    @Test
    void bigOrderReachesTheWarehouseAndTheFraudCheck() {
        Placements.using(queue).place("A-1", 15_000);

        assertAll(
                () -> assertThat(queue.delivered("orders")).as("orders").containsExactly("A-1 15000"),
                () -> assertThat(queue.delivered("fraud-checks")).as("fraud checks").containsExactly("A-1 15000"));
    }

    @Test
    void smallOrderSkipsTheFraudCheck() {
        Placements.using(queue).place("A-2", 2_500);

        assertAll(
                () -> assertThat(queue.delivered("orders")).as("orders").containsExactly("A-2 2500"),
                () -> assertThat(queue.delivered("fraud-checks")).as("fraud checks").isEmpty());
    }
}
