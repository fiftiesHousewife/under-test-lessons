package io.github.fiftieshousewife.undertest.techniques.flaky.fixed;

import io.github.fiftieshousewife.undertest.techniques.flaky.MailServer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FIXED: the same mail server on a quiet machine, where every message goes at once. The code never needed fixing: the
 * fix is in the tests, which wait until the message has gone instead of guessing how long it takes.
 */
public final class QuietMailServer implements MailServer {

    private final List<String> outbox = new CopyOnWriteArrayList<>();

    @Override
    public void send(final String message) {
        outbox.add(message);
    }

    @Override
    public List<String> sent() {
        return List.copyOf(outbox);
    }
}
