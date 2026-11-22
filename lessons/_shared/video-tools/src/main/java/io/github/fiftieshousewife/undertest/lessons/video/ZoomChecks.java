package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks a script's zooms on key code before anything is recorded: each zoom's lines are among the lines its panel
 * shows, a scene has at most one zoom, and each zoom's scene leaves it time to ease in, hold and ease out.
 */
final class ZoomChecks {

    private static final Pattern TYPED = Pattern.compile("^Type(?:@\\S+)?\\s+([\"`])(.*)\\1\\s*$");
    private static final Pattern SCENE = Pattern.compile("^#\\s*-+\\s*Scene\\s+(\\d+):.*");

    private ZoomChecks() {
    }

    /** Problems with the zooms in a script, each naming its scene. */
    static List<Problem> problems(final Path root, final Path tape) {
        final List<Problem> problems = new ArrayList<>(shownAndOnePerScene(root, tape));
        problems.addAll(timeToZoom(root, tape));
        return problems;
    }

    private static List<Problem> shownAndOnePerScene(final Path root, final Path tape) {
        final List<Problem> problems = new ArrayList<>();
        final Path lesson = tape.toAbsolutePath().resolveSibling("..").normalize();
        final List<Path> places = List.of(lesson, root.resolve("lessons/_shared"), root.resolve("build-logic"),
                root.resolve("gradle"));
        String scene = "?";
        int zoomsInScene = 0;
        for (final String line : lines(tape)) {
            final Matcher heading = SCENE.matcher(line);
            final Matcher typed = TYPED.matcher(line);
            if (heading.matches()) {
                scene = heading.group(1);
                zoomsInScene = 0;
            } else if (typed.matches() && typed.group(2).startsWith("show ")) {
                final List<String> words = SceneContent.words(typed.group(2));
                final Set<Integer> zoomed = SceneContent.zoomed(words);
                if (zoomed.isEmpty()) {
                    continue;
                }
                if (++zoomsInScene == 2) {
                    problems.add(new Problem(tape, "scene " + scene + " zooms twice; a scene zooms on one place"));
                }
                try {
                    final SceneContent.Panel panel = SceneContent.panel(root, places, words.subList(1, words.size()));
                    final String where = "scene " + scene;
                    notShown(zoomed, panel).ifPresent(missing -> problems.add(new Problem(tape,
                            where + " zooms on line " + missing + ", which its panel doesn't show")));
                } catch (IllegalArgumentException e) {
                    continue; // a show line that finds no file, which LessonChecks.shows reports
                }
            }
        }
        return problems;
    }

    /** The first zoomed line the panel doesn't show, if any. */
    private static Optional<Integer> notShown(final Set<Integer> zoomed, final SceneContent.Panel panel) {
        final List<Integer> shown = panel.lines().stream().map(LiveOutputs::lineNumber).toList();
        return zoomed.stream().filter(line -> !shown.contains(line)).findFirst();
    }

    /** Each zoom's scene shows it long enough; scenes with live runs are checked once a recording has timed them. */
    private static List<Problem> timeToZoom(final Path root, final Path tape) {
        final List<Zoom.Span> spans;
        try {
            spans = TapeTimeline.zooms(root, tape, measured(tape));
        } catch (IllegalStateException | UncheckedIOException e) {
            return List.of(); // a live run not yet timed, or a missing Source file, which another check reports
        }
        final double needed = 2 * (Zoom.DELAY + Zoom.EASE) + Zoom.SHORTEST_HOLD;
        final List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < spans.size(); i++) {
            final double shown = spans.get(i).end() - spans.get(i).start();
            if (shown < needed) {
                problems.add(new Problem(tape, String.format(Locale.ROOT, "zoom %d shows for %.1f s after its code"
                        + " appears, but needs %.1f s to ease in, hold and ease out; lengthen its scene's last Sleep",
                        i + 1, shown, needed)));
            }
        }
        return problems;
    }

    private static List<Double> measured(final Path tape) {
        final Path timings = tape.resolveSibling(String.valueOf(tape.getFileName()).replace(".tape", ".timings"));
        return Files.isRegularFile(timings) ? lines(timings).stream().filter(line -> !line.isBlank())
                .map(line -> Double.valueOf(line.strip())).toList() : List.of();
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
