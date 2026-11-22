package io.github.fiftieshousewife.undertest.techniques.capture;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

/** Where the capture is written: every request and response, in the order Logbook wrote them. */
public final class CaptureLog implements HttpLogWriter {

    private final List<String> written = new CopyOnWriteArrayList<>();

    @Override
    public void write(final Precorrelation precorrelation, final String request) {
        written.add(request);
    }

    @Override
    public void write(final Correlation correlation, final String response) {
        written.add(response);
    }

    /** Everything written so far, one exchange after another. */
    public String text() {
        return String.join("\n", written);
    }
}
