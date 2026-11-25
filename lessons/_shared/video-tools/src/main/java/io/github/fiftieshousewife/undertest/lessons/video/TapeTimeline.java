package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Works out when each scene of a lesson video is on screen, from its VHS script alone. Only time on camera counts:
 * pauses ({@code Sleep}) and visible typing. Anything done while hidden ({@code Hide} ... {@code Show}) takes no video
 * time. Scenes start at comment lines like {@code # ---- Scene 2: the build ----}. A {@code Wait} on camera lasts as
 * long as the command it waits for, so its time comes from the measured run times the recording writes (one per live
 * run, in order); without them, a {@code Wait} on camera is an error. Typing goes at VHS's default speed until the
 * script sets another.
 */
public final class TapeTimeline {

    private static final Pattern SCENE = Pattern.compile("^#\\s*-+\\s*Scene\\s+\\d+:\\s*(.*?)\\s*-+\\s*$");
    private static final Pattern SLEEP = Pattern.compile("^Sleep\\s+(\\d+(?:\\.\\d+)?)(ms|s)\\s*$");
    private static final Pattern SPEED = Pattern.compile("^Set\\s+TypingSpeed\\s+(\\d+(?:\\.\\d+)?)(ms|s)\\s*$");
    private static final Set<String> HIDE_OR_SHOW = Set.of("Hide", "Show");
    private static final Pattern SOURCE = Pattern.compile("^Source\\s+(\\S+)\\s*$");

    private final Path root;
    private final java.util.Deque<Double> measured;
    private final List<Scene> collected = new ArrayList<>();
    private final List<ZoomTiming.Span> zoomSpans = new ArrayList<>();
    private final List<Double> zoomStarts = new ArrayList<>();
    private double time;
    private boolean hidden;
    private double typingSeconds = 0.05;
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
        return List.copyOf(readAll(root, tape, measured).zoomSpans);
    }

    /** As {@link #scenes(Path, Path)}, using measured run times, in seconds, for each {@code Wait} on camera. */
    public static List<Scene> scenes(final Path root, final Path tape, final List<Double> measured) {
        return List.copyOf(readAll(root, tape, measured).collected);
    }

    private static TapeTimeline readAll(final Path root, final Path tape, final List<Double> measured) {
        final TapeTimeline timeline = new TapeTimeline(root, measured);
        timeline.read(tape);
        timeline.endScene();
        return timeline;
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
        } else if (HIDE_OR_SHOW.contains(line)) {
            hidden = "Hide".equals(line);
        } else if (!hidden && line.startsWith("Wait")) {
            time += measuredRun(where);
        } else {
            timeAndSettings(line);
        }
    }

    private double measuredRun(final String where) {
        if (measured.isEmpty()) {
            throw new IllegalStateException(where + ": a Wait on camera needs a measured run time; record the"
                    + " video first (its live runs use run_live, which measures them)");
        }
        return measured.removeFirst();
    }

    private void timeAndSettings(final String line) {
        final Matcher sleep = SLEEP.matcher(line);
        final Matcher speed = SPEED.matcher(line);
        final Matcher source = SOURCE.matcher(line);
        if (speed.matches()) {
            typingSeconds = VhsTime.seconds(speed.group(1), speed.group(2));
        } else if (source.matches()) {
            read(root.resolve(source.group(1)));
        } else if (sleep.matches()) {
            addOnCamera(VhsTime.seconds(sleep.group(1), sleep.group(2)));
        } else if (line.startsWith("Type")) {
            typed(line);
        }
    }

    private void typed(final String line) {
        endZooms(); // the scene draws something else, which a zoom would hide: the zoom has ended
        if (line.contains("show ") && line.contains("--zoom")) {
            zoomStarts.add(time); // the code appears when the picture next shows, at this moment
        }
        addOnCamera(VhsTime.typing(line, typingSeconds));
    }

    private void addOnCamera(final double seconds) {
        if (!hidden) {
            time += seconds;
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

    private static List<String> readLines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
