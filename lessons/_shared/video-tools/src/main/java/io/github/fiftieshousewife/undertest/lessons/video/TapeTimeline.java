package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Works out when each scene of a lesson video is on screen, from its VHS script alone. Only time on camera counts:
 * pauses ({@code Sleep}) and visible typing. Anything done while hidden ({@code Hide} ... {@code Show}) takes no video
 * time. Scenes start at comment lines like {@code # ---- Scene 2: the build ----}. A {@code Wait} on camera lasts as
 * long as the command it waits for, so its time comes from the measured run times the recording writes (one per live
 * run, in order); without them, a {@code Wait} on camera is an error.
 */
public final class TapeTimeline {

    private static final Pattern SCENE = Pattern.compile("^#\\s*-+\\s*Scene\\s+\\d+:\\s*(.*?)\\s*-+\\s*$");
    private static final Pattern SLEEP = Pattern.compile("^Sleep\\s+(\\d+(?:\\.\\d+)?)(ms|s)\\s*$");
    private static final Pattern SPEED = Pattern.compile("^Set\\s+TypingSpeed\\s+(\\d+(?:\\.\\d+)?)(ms|s)\\s*$");
    private static final Pattern SOURCE = Pattern.compile("^Source\\s+(\\S+)\\s*$");
    private static final double VHS_DEFAULT_TYPING_SECONDS = 0.05;

    private final Path root;
    private final java.util.Deque<Double> measured;
    private final List<Scene> collected = new ArrayList<>();
    private final List<ZoomTiming.Span> zoomSpans = new ArrayList<>();
    private final List<Double> zoomStarts = new ArrayList<>();
    private double time;
    private boolean hidden;
    private double typingSeconds = VHS_DEFAULT_TYPING_SECONDS;
    private String title;
    private double sceneStart;

    private TapeTimeline(final Path root, final List<Double> measured) {
        this.root = root;
        this.measured = new java.util.ArrayDeque<>(measured);
    }

    /** The scenes of a script, in order. Paths in {@code Source} lines are relative to {@code root}. */
    public static List<Scene> scenes(final Path root, final Path tape) {
        return scenes(root, tape, List.of());
    }

    /**
     * When each zoom on key code ({@code show ... --zoom}) is on screen: from the moment its code appears until the
     * scene draws anything else (a callout, a panel, a note, a run) or ends, in the order the script has them.
     * Measured run times as for {@link #scenes(Path, Path, List)}.
     */
    public static List<ZoomTiming.Span> zooms(final Path root, final Path tape, final List<Double> measured) {
        final TapeTimeline timeline = new TapeTimeline(root, measured);
        timeline.read(tape);
        timeline.endScene();
        return List.copyOf(timeline.zoomSpans);
    }

    /** As {@link #scenes(Path, Path)}, using measured run times, in seconds, for each {@code Wait} on camera. */
    public static List<Scene> scenes(final Path root, final Path tape, final List<Double> measured) {
        final TapeTimeline timeline = new TapeTimeline(root, measured);
        timeline.read(tape);
        timeline.endScene();
        return List.copyOf(timeline.collected);
    }

    private void read(final Path tape) {
        final List<String> lines = readLines(tape);
        for (int i = 0; i < lines.size(); i++) {
            step(lines.get(i).strip(), tape.getFileName() + " line " + (i + 1));
        }
    }

    private void step(final String line, final String where) {
        final Matcher scene = SCENE.matcher(line);
        if (scene.matches()) {
            endScene();
            title = scene.group(1);
            sceneStart = time;
        } else if ("Hide".equals(line) || "Show".equals(line)) {
            hidden = "Hide".equals(line);
        } else if (!hidden && line.startsWith("Wait")) {
            if (measured.isEmpty()) {
                throw new IllegalStateException(where + ": a Wait on camera needs a measured run time; record the"
                        + " video first (its live runs use run_live, which measures them)");
            }
            time += measured.removeFirst();
        } else {
            timeAndSettings(line);
        }
    }

    private void timeAndSettings(final String line) {
        final Matcher sleep = SLEEP.matcher(line);
        final Matcher speed = SPEED.matcher(line);
        final Matcher source = SOURCE.matcher(line);
        if (speed.matches()) {
            typingSeconds = seconds(speed.group(1), speed.group(2));
        } else if (source.matches()) {
            read(root.resolve(source.group(1)));
        } else if (!hidden && sleep.matches()) {
            time += seconds(sleep.group(1), sleep.group(2));
        } else if (line.startsWith("Type")) {
            endZooms(); // the scene draws something else, which a zoom would hide: the zoom has ended
            if (line.contains("show ") && line.contains("--zoom")) {
                zoomStarts.add(time); // the code appears when the picture next shows, at this moment
            }
            if (!hidden) {
                time += typingTime(line);
            }
        }
    }

    private void endScene() {
        if (title != null) {
            collected.add(new Scene(title, sceneStart, time));
        }
        endZooms();
    }

    private void endZooms() {
        zoomStarts.forEach(start -> zoomSpans.add(new ZoomTiming.Span(start, time)));
        zoomStarts.clear();
    }

    /* Time to type a {@code Type} line's text: its length times its own speed ({@code Type@40ms}) or the default. */
    double typingTime(final String line) {
        final int space = line.indexOf(' ');
        final String rest = space < 0 ? "" : line.substring(space + 1).strip();
        final boolean quoted = rest.length() >= 2 && "\"'`".indexOf(rest.charAt(0)) >= 0
                && rest.charAt(rest.length() - 1) == rest.charAt(0);
        if (!quoted) {
            return 0;
        }
        final String command = space < 0 ? line : line.substring(0, space);
        final double perCharacter = command.startsWith("Type@") ? duration(command.substring("Type@".length()))
                : typingSeconds;
        return perCharacter * (rest.length() - 2);
    }

    /* A VHS duration such as {@code 40ms} or {@code 1.5s}, in seconds. */
    static double duration(final String text) {
        return text.endsWith("ms") ? seconds(text.substring(0, text.length() - 2), "ms")
                : seconds(text.substring(0, text.length() - 1), "s");
    }

    static double seconds(final String amount, final String unit) {
        final double value = Double.parseDouble(amount);
        return "ms".equals(unit) ? value / 1000 : value;
    }

    private static List<String> readLines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
