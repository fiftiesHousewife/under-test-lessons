package io.github.fiftieshousewife.undertest.techniques.capture;

import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;

/** How the service's exchanges are captured: the Logbook that records them, writing to the given place. */
public interface Capture {

    /** A Logbook that records every request and response and hands each one, as text, to the writer. */
    Logbook logbook(HttpLogWriter writer);
}
