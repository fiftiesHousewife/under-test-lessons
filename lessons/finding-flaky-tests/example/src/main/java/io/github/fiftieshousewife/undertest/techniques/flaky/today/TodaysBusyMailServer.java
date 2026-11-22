package io.github.fiftieshousewife.undertest.techniques.flaky.today;

import io.github.fiftieshousewife.undertest.techniques.flaky.MailServer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODAY: the mail server as it runs on today's busy, shared build machine. Most messages go at once, but every third
 * one waits 600 ms in a queue. Nothing is wrong with the code: some of the time, it is just slower.
 */
public final class TodaysBusyMailServer implements MailServer {

    private static final Duration BUSY = Duration.ofMillis(600);

    private final AtomicInteger messages = new AtomicInteger();
    private final List<String> outbox = new CopyOnWriteArrayList<>();

    @Override
    public void send(final String message) {
        if (messages.incrementAndGet() % 3 == 0) {
            waitInTheQueue();
        }
        outbox.add(message);
    }

    @Override
    public List<String> sent() {
        return List.copyOf(outbox);
    }

    private static void waitInTheQueue() {
        try {
            Thread.sleep(BUSY);
        } catch (final InterruptedException stopped) {
            Thread.currentThread().interrupt();
        }
    }
}
