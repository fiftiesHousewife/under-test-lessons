package io.github.fiftieshousewife.undertest.lessons.video;

import io.github.fiftieshousewife.undertest.lessons.video.Zoom.Geometry;
import io.github.fiftieshousewife.undertest.lessons.video.Zoom.Rows;
import io.github.fiftieshousewife.undertest.lessons.video.Zoom.Size;
import io.github.fiftieshousewife.undertest.lessons.video.Zoom.Window;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
import lombok.extern.slf4j.Slf4j;

/* The silent recording with {@link Zoom}'s zooms drawn into each frame by ffmpeg. */
@Slf4j
final class ZoomedRecording {

    private ZoomedRecording() {
    }

    /*
     * The silent recording with its zooms, as {@code <id>.zoomed.mp4}; the silent recording itself when the script
     * has no zooms, or when the rows the recording reported don't pair up with them (said in the log).
     */
    static Path zoomed(final Media media) {
        final Path silent = media.file(".silent.mp4");
        final List<ZoomTiming.Span> spans = Recording.zooms(media);
        if (spans.isEmpty()) {
            return silent;
        }
        final List<Optional<Rows>> rows = Zoom.rows(media.file(".zooms"));
        if (rows.size() != spans.size()) {
            log.warn("{}: the script has {} zooms but the recording reported rows for {}; made without zooming",
                    LogSafe.lesson(media), spans.size(), rows.size());
            return silent;
        }
        final String filter = filter(media, spans, rows);
        return filter.isEmpty() ? silent : drawn(media, filter);
    }

    private static Path drawn(final Media media, final String filter) {
        final Path zoomed = media.file(".zoomed.mp4");
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error", "-i",
                media.file(".silent.mp4").toString(), "-vf", filter, "-c:v", "libx264", "-pix_fmt", "yuv420p",
                zoomed.toString()), media.folder());
        return zoomed;
    }

    /* The filter for the zooms whose rows the recording reported; each other one is left out, said in the log. */
    private static String filter(final Media media, final List<ZoomTiming.Span> spans,
            final List<Optional<Rows>> rows) {
        final Geometry geometry = Zoom.geometry(media.file(".tape"));
        final List<ZoomTiming.Span> used = new ArrayList<>();
        final List<Window> windows = new ArrayList<>();
        for (int i = 0; i < spans.size(); i++) {
            if (rows.get(i).isPresent()) {
                used.add(spans.get(i));
                windows.add(Zoom.window(geometry, rows.get(i).get()));
            } else {
                log.warn("{}: zoom {} has no rows (the terminal didn't say where the cursor was); left out",
                        LogSafe.lesson(media), i + 1);
            }
        }
        return filter(geometry, used, windows);
    }

    /*
     * The ffmpeg filter for the zooms: scale each frame up by the zoom at that moment, then crop it back to the frame's
     * size at the window. Empty when there is nothing to zoom. Spans and windows pair up in order.
     */
    static String filter(final Geometry geometry, final List<ZoomTiming.Span> spans, final List<Window> windows) {
        final int count = Math.min(spans.size(), windows.size());
        if (count == 0) {
            return "";
        }
        // The window at a moment moves from the whole frame to the zoom's window as the zoom's amount goes from 0 to 1;
        // spans never overlap, so summing each zoom's share gives the one in progress. Scaling the frame up by
        // width / window width and cropping at the window's corner (scaled too) shows exactly that window.
        final Size frame = geometry.frame();
        final List<String> shrink = terms(spans, windows, window -> 1 - window.width() / frame.width());
        final List<String> left = terms(spans, windows, Window::left);
        final List<String> top = terms(spans, windows, Window::top);
        final String zoom = "(1/(1-(" + String.join("+", shrink) + ")))";
        return String.format(Locale.ROOT, "scale=w='trunc((iw*%1$s+1)/2)*2':h='trunc((ih*%1$s+1)/2)*2':eval=frame,"
                + "crop=w=%2$d:h=%3$d:x='(%4$s)*%1$s':y='(%5$s)*%1$s'", zoom, frame.width(), frame.height(),
                String.join("+", left), String.join("+", top));
    }

    /* For each zoom, a number about its window times how far in the zoom is at the frame's time. */
    private static List<String> terms(final List<ZoomTiming.Span> spans, final List<Window> windows,
            final ToDoubleFunction<Window> part) {
        final List<String> terms = new ArrayList<>();
        for (int i = 0; i < Math.min(spans.size(), windows.size()); i++) {
            terms.add(number(part.applyAsDouble(windows.get(i))) + "*" + amount(spans.get(i)));
        }
        return terms;
    }

    /* {@link ZoomTiming.Span#amount} as an ffmpeg expression of the frame's time, {@code t}. */
    private static String amount(final ZoomTiming.Span span) {
        if (span.end() - span.start() < 2 * (ZoomTiming.DELAY + ZoomTiming.EASE) + ZoomTiming.SHORTEST_HOLD) {
            return "0";
        }
        final String ease = number(ZoomTiming.EASE);
        final String in = String.format(Locale.ROOT, "(t-%s)/%s", number(span.start() + ZoomTiming.DELAY), ease);
        final String out = String.format(Locale.ROOT, "(%s-t)/%s", number(span.end() - ZoomTiming.DELAY), ease);
        return String.format(Locale.ROOT, "if(between(t,%s,%s),(1-cos(PI*clip(min(%s,%s),0,1)))/2,0)",
                number(span.start()), number(span.end()), in, out);
    }

    private static String number(final double value) {
        return String.format(Locale.ROOT, "%.4f", value);
    }
}
