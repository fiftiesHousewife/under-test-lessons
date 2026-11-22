package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * The zoom on key code: {@code show <file> <from> <to> --zoom 22-24} marks those lines, and while the scene is on
 * screen the picture eases in on them, holds, and eases back out. The terminal helper reports which screen rows the
 * lines landed on ({@code <id>.zooms}, one line per zoom: first row, last row, rows on screen); the script says when
 * ({@link TapeTimeline#zooms}); this class works out the window and writes the ffmpeg filter that scales and crops
 * each frame.
 */
@Slf4j
final class Zoom {

    /** The furthest the picture zooms in. */
    static final double MAX_ZOOM = 1.8;
    /** Less magnification than this isn't worth the movement, so the picture stays as it is. */
    static final double SMALLEST_ZOOM = 1.15;
    /** Seconds after the code appears before the zoom starts, and before the scene ends that it is back out. */
    static final double DELAY = 1.0;
    /** Seconds the zoom takes to ease in, and to ease out. */
    static final double EASE = 0.8;
    /** The shortest hold worth zooming for, in seconds. */
    static final double SHORTEST_HOLD = 1.0;

    private static final Pattern SET = Pattern.compile("^Set\\s+(Width|Height|Padding|WindowBar)\\s+(\\S+)\\s*$");
    private static final Pattern REPORTED_ROWS = Pattern.compile("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*$");
    /** VHS's defaults, and the height of its window bar. */
    private static final int VHS_WIDTH = 1200;
    private static final int VHS_HEIGHT = 600;
    private static final int VHS_PADDING = 60;
    private static final int WINDOW_BAR = 30;

    /** A picture's width and height in pixels. */
    record Size(int width, int height) {
    }

    /** The recorded frame: its size, the padding around the terminal, and the window bar's height (0 for none). */
    record Geometry(Size frame, int padding, int bar) {
    }

    /** Where the helper printed the key lines: the first and last screen row (from 1), and the rows on screen. */
    record Rows(int first, int last, int screenRows) {
    }

    /** A point in the frame, in pixels from its top-left corner. */
    record Corner(double left, double top) {
    }

    /** The part of the frame to show at full zoom, in the frame's pixels: its top-left corner, width and height. */
    record Window(Corner corner, double width, double height) {

        double left() {
            return corner.left();
        }

        double top() {
            return corner.top();
        }
    }

    /** When a zoom's code is on screen, in seconds of video: from when it appears to the end of its scene. */
    record Span(double start, double end) {

        /** How far in the zoom is at a moment: 0 (not at all) to 1 (fully), eased at both ends. */
        double amount(final double time) {
            if (end - start < 2 * (DELAY + EASE) + SHORTEST_HOLD) {
                return 0;
            }
            final double in = (time - start - DELAY) / EASE;
            final double out = (end - DELAY - time) / EASE;
            final double progress = Math.max(0, Math.min(1, Math.min(in, out)));
            return (1 - Math.cos(Math.PI * progress)) / 2;
        }

        Span scaled(final double factor) {
            return new Span(start * factor, end * factor);
        }
    }

    private Zoom() {
    }

    /** The frame a script records, from its {@code Set Width}, {@code Height}, {@code Padding}, {@code WindowBar}. */
    static Geometry geometry(final Path tape) {
        int width = VHS_WIDTH;
        int height = VHS_HEIGHT;
        int padding = VHS_PADDING;
        int bar = 0;
        for (final String line : lines(tape)) {
            final Matcher set = SET.matcher(line.strip());
            if (set.matches()) {
                switch (set.group(1)) {
                    case "Width" -> width = Integer.parseInt(set.group(2));
                    case "Height" -> height = Integer.parseInt(set.group(2));
                    case "Padding" -> padding = Integer.parseInt(set.group(2));
                    default -> bar = WINDOW_BAR;
                }
            }
        }
        return new Geometry(new Size(width, height), padding, bar);
    }

    /**
     * The window at full zoom: the key rows with a row of room above and below, as large as {@link #MAX_ZOOM} allows,
     * kept inside the frame and against its left edge, where code starts. The whole frame if zooming isn't worth it.
     */
    static Window window(final Geometry geometry, final Rows rows) {
        final Size frame = geometry.frame();
        final double top = geometry.bar() + geometry.padding();
        final double rowHeight = (frame.height() - top - geometry.padding()) / (double) rows.screenRows();
        final double regionTop = top + (rows.first() - 2) * rowHeight;
        final double regionBottom = top + (rows.last() + 1) * rowHeight;
        final double height = Math.min(frame.height(), Math.max(frame.height() / MAX_ZOOM, regionBottom - regionTop));
        if (frame.height() / height < SMALLEST_ZOOM) {
            return new Window(new Corner(0, 0), frame.width(), frame.height());
        }
        final double centred = (regionTop + regionBottom - height) / 2;
        return new Window(new Corner(0, Math.max(0, Math.min(frame.height() - height, centred))),
                height * frame.width() / frame.height(), height);
    }

    /** The rows the helper reported, one zoom a line, in order; empty where it couldn't find them ("unknown"). */
    static List<Optional<Rows>> rows(final Path zooms) {
        if (!Files.isRegularFile(zooms)) {
            return List.of();
        }
        final List<Optional<Rows>> rows = new ArrayList<>();
        for (final String line : lines(zooms)) {
            final Matcher numbers = REPORTED_ROWS.matcher(line);
            rows.add(numbers.matches() ? Optional.of(new Rows(Integer.parseInt(numbers.group(1)),
                    Integer.parseInt(numbers.group(2)), Integer.parseInt(numbers.group(3)))) : Optional.empty());
        }
        return rows;
    }

    /**
     * The silent recording with its zooms, as {@code <id>.zoomed.mp4}; the silent recording itself when the script
     * has no zooms, or when the rows the recording reported don't pair up with them (said in the log).
     */
    static Path zoomed(final Media media) {
        final Path silent = media.file(".silent.mp4");
        final List<Span> spans = media.zooms();
        if (spans.isEmpty()) {
            return silent;
        }
        final List<Optional<Rows>> rows = rows(media.file(".zooms"));
        if (rows.size() != spans.size()) {
            log.warn("{}: the script has {} zooms but the recording reported rows for {}; made without zooming",
                    media.safeLesson(), spans.size(), rows.size());
            return silent;
        }
        final Geometry geometry = geometry(media.file(".tape"));
        final List<Span> used = new ArrayList<>();
        final List<Window> windows = new ArrayList<>();
        for (int i = 0; i < spans.size(); i++) {
            if (rows.get(i).isPresent()) {
                used.add(spans.get(i));
                windows.add(window(geometry, rows.get(i).get()));
            } else {
                log.warn("{}: zoom {} has no rows (the terminal didn't say where the cursor was); left out",
                        media.safeLesson(), i + 1);
            }
        }
        final String filter = filter(geometry, used, windows);
        if (filter.isEmpty()) {
            return silent;
        }
        final Path zoomed = media.file(".zoomed.mp4");
        ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error", "-i", silent.toString(),
                "-vf", filter, "-c:v", "libx264", "-pix_fmt", "yuv420p", zoomed.toString()), media.folder());
        return zoomed;
    }

    /**
     * The ffmpeg filter for the zooms: scale each frame up by the zoom at that moment, then crop it back to the frame's
     * size at the window. Empty when there is nothing to zoom. Spans and windows pair up in order.
     */
    static String filter(final Geometry geometry, final List<Span> spans, final List<Window> windows) {
        final int count = Math.min(spans.size(), windows.size());
        if (count == 0) {
            return "";
        }
        // The window at a moment moves from the whole frame to the zoom's window as the zoom's amount goes from 0 to 1;
        // spans never overlap, so summing each zoom's share gives the one in progress. Scaling the frame up by
        // width / window width and cropping at the window's corner (scaled too) shows exactly that window.
        final Size frame = geometry.frame();
        final List<String> shrink = new ArrayList<>();
        final List<String> left = new ArrayList<>();
        final List<String> top = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final String amount = amount(spans.get(i));
            final Window window = windows.get(i);
            shrink.add(number(1 - window.width() / frame.width()) + "*" + amount);
            left.add(number(window.left()) + "*" + amount);
            top.add(number(window.top()) + "*" + amount);
        }
        final String zoom = "(1/(1-(" + String.join("+", shrink) + ")))";
        return String.format(Locale.ROOT, "scale=w='trunc((iw*%1$s+1)/2)*2':h='trunc((ih*%1$s+1)/2)*2':eval=frame,"
                + "crop=w=%2$d:h=%3$d:x='(%4$s)*%1$s':y='(%5$s)*%1$s'", zoom, frame.width(), frame.height(),
                String.join("+", left), String.join("+", top));
    }

    /** {@link Span#amount} as an ffmpeg expression of the frame's time, {@code t}. */
    private static String amount(final Span span) {
        if (span.end() - span.start() < 2 * (DELAY + EASE) + SHORTEST_HOLD) {
            return "0";
        }
        final String in = String.format(Locale.ROOT, "(t-%s)/%s", number(span.start() + DELAY), number(EASE));
        final String out = String.format(Locale.ROOT, "(%s-t)/%s", number(span.end() - DELAY), number(EASE));
        return String.format(Locale.ROOT, "if(between(t,%s,%s),(1-cos(PI*clip(min(%s,%s),0,1)))/2,0)",
                number(span.start()), number(span.end()), in, out);
    }

    private static String number(final double value) {
        return String.format(Locale.ROOT, "%.4f", value);
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
