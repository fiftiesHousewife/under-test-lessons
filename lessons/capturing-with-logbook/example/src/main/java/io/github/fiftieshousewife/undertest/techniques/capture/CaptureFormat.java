package io.github.fiftieshousewife.undertest.techniques.capture;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpMessage;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Origin;
import org.zalando.logbook.Precorrelation;

/**
 * Writes each request and response as plain HTTP: what happened, its headers, and its body. It leaves out Logbook's
 * correlation ids and timings, which differ on every run, so a capture can be compared with an approved file.
 */
public final class CaptureFormat implements HttpLogFormatter {

    @Override
    public String format(final Precorrelation precorrelation, final HttpRequest request) {
        final String what = request.getOrigin() == Origin.REMOTE ? "Request received" : "Request sent";
        final String query = request.getQuery().isEmpty() ? "" : "?" + request.getQuery();
        return message(what + ": " + request.getMethod() + " " + request.getPath() + query, request);
    }

    @Override
    public String format(final Correlation correlation, final HttpResponse response) {
        final String what = response.getOrigin() == Origin.LOCAL ? "Response sent" : "Response received";
        return message(what + ": " + response.getStatus(), response);
    }

    private static String message(final String firstLine, final HttpMessage message) {
        final String headers = message.getHeaders().entrySet().stream()
                .map(CaptureFormat::header)
                .collect(Collectors.joining());
        final String body = bodyOf(message);
        return firstLine + "\n" + headers + (body.isEmpty() ? "" : "\n" + body + "\n");
    }

    private static String header(final Map.Entry<String, List<String>> header) {
        return header.getKey() + ": " + String.join(", ", header.getValue()) + "\n";
    }

    private static String bodyOf(final HttpMessage message) {
        try {
            return message.getBodyAsString();
        } catch (IOException e) {
            return "(the body could not be read: " + e.getMessage() + ")";
        }
    }
}
