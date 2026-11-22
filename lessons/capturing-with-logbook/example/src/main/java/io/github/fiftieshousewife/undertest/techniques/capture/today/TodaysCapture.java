package io.github.fiftieshousewife.undertest.techniques.capture.today;

import io.github.fiftieshousewife.undertest.techniques.capture.Capture;
import io.github.fiftieshousewife.undertest.techniques.capture.CaptureFormat;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.HeaderFilters;

/**
 * TODAY: the capture as it was first set up. It removes the headers that change on every run, so captures can be
 * compared. But giving Logbook a header filter replaces its default one, which hides the Authorization header, and
 * nothing touches the customer's email in the bodies: both are written as they are.
 */
public final class TodaysCapture implements Capture {

    @Override
    public Logbook logbook(final HttpLogWriter writer) {
        return Logbook.builder()
                .headerFilter(HeaderFilters.removeHeaders("Date", "Host", "User-Agent", "Content-Length"))
                .sink(new DefaultSink(new CaptureFormat(), writer))
                .build();
    }
}
