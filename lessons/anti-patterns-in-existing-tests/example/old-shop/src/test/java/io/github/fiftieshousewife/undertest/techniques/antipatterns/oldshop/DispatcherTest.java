package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class DispatcherTest {

    private final List<Long> sent = new CopyOnWriteArrayList<>();

    @Test
    void sendsThePaidOrderToTheWarehouse() throws InterruptedException {
        new Dispatcher(new RecordingWarehouse(sent, 0), Executors.newSingleThreadExecutor()).dispatch(41);
        Thread.sleep(500);
        assertEquals(List.of(41L), sent);
    }

    @Test
    void triesAgainWhenTheWarehouseIsBusy() throws InterruptedException {
        new Dispatcher(new RecordingWarehouse(sent, 1), Executors.newSingleThreadExecutor()).dispatch(42);
        TimeUnit.SECONDS.sleep(1);
        assertEquals(List.of(42L), sent);
    }

    /** A warehouse that refuses its first few sends, then records each order it is sent. */
    private static final class RecordingWarehouse implements Warehouse {

        private final List<Long> sent;
        private int refusals;

        RecordingWarehouse(final List<Long> sent, final int refusals) {
            this.sent = sent;
            this.refusals = refusals;
        }

        @Override
        public boolean reserve(final long orderNumber) {
            return true;
        }

        @Override
        public void send(final long orderNumber) {
            if (refusals > 0) {
                refusals--;
                throw new IllegalStateException("busy");
            }
            sent.add(orderNumber);
        }
    }
}
