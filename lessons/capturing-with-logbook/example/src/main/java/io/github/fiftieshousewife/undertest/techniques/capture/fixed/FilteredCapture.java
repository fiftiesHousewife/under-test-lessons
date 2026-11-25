package io.github.fiftieshousewife.undertest.techniques.capture.fixed;

import io.github.fiftieshousewife.undertest.techniques.capture.Capture;
import io.github.fiftieshousewife.undertest.techniques.capture.CaptureFormat;
import java.util.Set;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.json.JsonBodyFilters;

/**
 * FIXED: the same capture, with Logbook's filters doing the masking before anything is written. The Authorization
 * header's value is replaced with XXX, and every "email" field in a JSON body with a made-up
 * address that no real person uses.
 */
public final class FilteredCapture implements Capture {

    @Override
    public Logbook logbook(final HttpLogWriter writer) {
        return Logbook.builder()
                .headerFilter(HeaderFilters.removeHeaders("Date", "Host", "User-Agent", "Content-Length"))
                .headerFilter(HeaderFilters.authorization())
                .bodyFilter(JsonBodyFilters.replaceJsonStringProperty(Set.of("email"), "customer@example.invalid"))
                .sink(new DefaultSink(new CaptureFormat(), writer))
                .build();
    }
}
