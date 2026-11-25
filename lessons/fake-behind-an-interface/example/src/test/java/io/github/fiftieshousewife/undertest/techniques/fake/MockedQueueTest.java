package io.github.fiftieshousewife.undertest.techniques.fake;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

/*
 * BEFORE: a mock of the queue. The test checks that each call was made, which repeats the code line by line. It can't
 * tell that the fraud-check message was sent after the commit, so it is never delivered.
 */
class MockedQueueTest {

    private final MessageQueue queue = mock(MessageQueue.class);

    @Test
    void bigOrderSendsBothMessagesAndCommits() {
        Placements.using(queue).place("A-1", 15_000);

        verify(queue).send("orders", "A-1 15000");
        verify(queue).send("fraud-checks", "A-1 15000");
        verify(queue).commit();
    }
}
