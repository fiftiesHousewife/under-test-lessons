package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * The zoom on key code: {@code show <file> <from> <to> --zoom 22-24} marks those lines, and while the scene is on
 * screen the picture eases in on them, holds, and eases back out. The terminal helper reports which screen rows the
 * lines landed on ({@code <id>.zooms}, one line per zoom: first row, last row, rows on screen); the script says when
 * ({@link TapeTimeline#zooms}); this class works out the window and writes the ffmpeg filter that scales and crops each
 * frame. The picture zooms in at most {@code MAX_ZOOM} times; less than {@code SMALLEST_ZOOM} isn't worth the movement,
 * so the picture stays as it is. The frame's size, padding and window bar are VHS's defaults unless the script sets
 * them.
 */
final class Zoom {

    static final double MAX_ZOOM = 1.8;
    static final double SMALLEST_ZOOM = 1.15;
    private static final Pattern SET = Pattern.compile("^Set\\s+(Width|Height|Padding|WindowBar)\\s+(\\S+)\\s*$");
    private static final Pattern REPORTED_ROWS = Pattern.compile("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*$");
    private static final int VHS_WIDTH = 1200;
    private static final int VHS_HEIGHT = 600;
    private static final int VHS_PADDING = 60;
    private static final int WINDOW_BAR = 30;

    /* A picture's width and height in pixels. */
    record Size(int width, int height) {
    }

    /* The recorded frame: its size, the padding around the terminal, and the window bar's height (0 for none). */
    record Geometry(Size frame, int padding, int bar) {
    }

    /* Where the helper printed the key lines: the first and last screen row (from 1), and the rows on screen. */
    record Rows(int first, int last, int screenRows) {
    }

    /* A point in the frame, in pixels from its top-left corner. */
    record Corner(double left, double top) {
    }

    /* The part of the frame to show at full zoom, in the frame's pixels: its top-left corner, width and height. */
    record Window(Corner corner, double width, double height) {

        double left() {
            return corner.left();
        }

        double top() {
            return corner.top();
        }
    }

    private Zoom() {
    }

    /* The frame a script records, from its {@code Set Width}, {@code Height}, {@code Padding}, {@code WindowBar}. */
    static Geometry geometry(final Path tape) {
        final Map<String, String> settings = new HashMap<>();
        for (final String line : lines(tape)) {
            final Matcher set = SET.matcher(line.strip());
            if (set.matches()) {
                settings.put(set.group(1), set.group(2));
            }
        }
        return new Geometry(new Size(setting(settings, "Width", VHS_WIDTH), setting(settings, "Height", VHS_HEIGHT)),
                setting(settings, "Padding", VHS_PADDING), settings.containsKey("WindowBar") ? WINDOW_BAR : 0);
    }

    private static int setting(final Map<String, String> settings, final String name, final int otherwise) {
        return settings.containsKey(name) ? Integer.parseInt(settings.get(name)) : otherwise;
    }

    /*
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

    /* The rows the helper reported, one zoom a line, in order; empty where it couldn't find them ("unknown"). */
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

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
