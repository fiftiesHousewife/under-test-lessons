package io.github.fiftieshousewife.undertest.techniques.flaky;

import java.util.List;

/** Sends order confirmations, and keeps a copy of each one so tests can look. */
public interface MailServer {

    /** Sends one message. The order desk calls it on a background thread. */
    void send(String message);

    /** Every message sent so far, oldest first. */
    List<String> sent();
}
