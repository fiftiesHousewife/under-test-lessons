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
 * Checks that catch what reviews found by hand: code panels that would show the wrong file or lines, scenes too short
 * for their narration, and slides without sources or repository links.
 */
final class LessonChecks {

    /** Spoken words per second, and the room a scene leaves around its narration. */
    static final double WORDS_PER_SECOND = 2.6;
    static final double ROOM = 1.35;
    static final double EXTRA_SECONDS = 1.5;

    private static final Pattern LESSON_LINK = Pattern.compile("(?m)^- lesson:.*\\(([a-z0-9-]+)\\)\\s*$");
    private static final Pattern SCENE = Pattern.compile("^#\\s*-+\\s*Scene\\s+\\d+:.*");
    private static final Pattern TYPED = Pattern.compile("^Type(?:@\\S+)?\\s+([\"`])(.*)\\1\\s*$");
    private static final Pattern SHOWN_FOR = Pattern.compile("^Show\\s*$");
    private static final Pattern SLEEP = Pattern.compile("^Sleep\\s+(\\d+(?:\\.\\d+)?)(ms|s)\\s*$");
    private static final Pattern PAUSE = Pattern.compile("\\[pause\\s+(\\d+(?:\\.\\d+)?)(ms|s)]");

    private LessonChecks() {
    }

    /** Each {@code show} line finds a file (in its lesson first) and any line range fits inside it. */
    static List<Problem> shows(final Path root, final Path tape) {
        final Path lesson = tape.toAbsolutePath().resolveSibling("..").normalize();
        final List<Path> places = List.of(lesson, root.resolve("lessons/_shared"), root.resolve("build-logic"),
                root.resolve("gradle"));
        final List<Problem> problems = new ArrayList<>();
        for (final String line : lines(tape)) {
            final Matcher typed = TYPED.matcher(line);
            final List<String> words = typed.matches() ? SceneContent.words(typed.group(2)) : List.of();
            if (words.size() > 1 && "show".equals(words.getFirst())) {
                problems.addAll(show(root, places, words));
            }
        }
        return problems.stream().map(message -> new Problem(tape, message.message())).toList();
    }

    private static List<Problem> show(final Path root, final List<Path> places, final List<String> words) {
        final Path file;
        try {
            file = SceneContent.find(root, places, words.get(1));
        } catch (IllegalArgumentException e) {
            return List.of(new Problem(root, e.getMessage()));
        }
        if (words.size() > 3 && words.get(2).matches("\\d+") && words.get(3).matches("\\d+")) {
            final int length = lines(file).size();
            if (Integer.parseInt(words.get(3)) > length) {
                return List.of(new Problem(root, "show " + words.get(1) + " asks for lines up to " + words.get(3)
                        + ", but " + file.getFileName() + " has " + length));
            }
        }
        return List.of();
    }

    /** Each scene without a live run shows for at least its narration's spoken length, with room. */
    static List<Problem> sceneTimes(final Path tape, final Path narration) {
        final List<List<String>> scenes = scenes(lines(tape));
        final List<String> paragraphs = Narration.rawParagraphs(narration);
        final List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < Math.min(scenes.size(), paragraphs.size()); i++) {
            final List<String> scene = scenes.get(i);
            final boolean live = scene.stream()
                    .anyMatch(line -> line.contains("run_live") || line.contains("run_with_notes"));
            final boolean shown = scene.stream().anyMatch(line -> SHOWN_FOR.matcher(line).matches());
            if (live || !shown) {
                continue;
            }
            final double needed = needed(paragraphs.get(i));
            final double onScreen = lastShownFor(scene);
            if (onScreen < needed) {
                problems.add(new Problem(tape, "scene %d shows for %.0f s but its narration needs %.0f s;"
                        .formatted(i + 1, onScreen, Math.ceil(needed)) + " lengthen its last Sleep"));
            }
        }
        return problems;
    }

    /** Seconds a narration paragraph needs on screen: spoken words, pauses, and room. */
    static double needed(final String paragraph) {
        double pauses = 0;
        final Matcher pause = PAUSE.matcher(paragraph);
        while (pause.find()) {
            pauses += seconds(pause.group(1), pause.group(2));
        }
        final String[] words = PAUSE.matcher(paragraph).replaceAll(" ").strip().split("\\s+");
        return (words.length / WORDS_PER_SECOND + pauses) * ROOM + EXTRA_SECONDS;
    }

    private static double lastShownFor(final List<String> scene) {
        double shown = 0;
        boolean afterShow = false;
        for (final String line : scene) {
            if (SHOWN_FOR.matcher(line).matches()) {
                afterShow = true;
            } else {
                final Matcher sleep = SLEEP.matcher(line);
                if (afterShow && sleep.matches()) {
                    shown = seconds(sleep.group(1), sleep.group(2));
                }
                afterShow = false;
            }
        }
        return shown;
    }

    /** A slides file names its sources, and its closing slides link the lessons repository by placeholder. */
    static List<Problem> slides(final Path slidesFile) {
        final String text = String.join("\n", lines(slidesFile));
        final List<Problem> problems = new ArrayList<>();
        if (!text.contains("Sources:")) {
            problems.add(new Problem(slidesFile, "no Sources: line; name where each fact on the slides comes from"));
        }
        if (!text.contains("{lessons-repo}")) {
            problems.add(new Problem(slidesFile, "no {lessons-repo} link on the closing slides"));
        }
        final Path lessons = slidesFile.toAbsolutePath().resolveSibling("../..").normalize();
        final Matcher link = LESSON_LINK.matcher(text);
        while (link.find()) {
            if (!Files.isRegularFile(lessons.resolve(link.group(1)).resolve("README.md"))) {
                problems.add(new Problem(slidesFile, "a slide links lesson " + link.group(1)
                        + ", which doesn't exist"));
            }
        }
        return problems;
    }

    private static List<List<String>> scenes(final List<String> lines) {
        final List<List<String>> scenes = new ArrayList<>();
        for (final String line : lines) {
            if (SCENE.matcher(line).matches()) {
                scenes.add(new ArrayList<>());
            } else if (!scenes.isEmpty()) {
                scenes.getLast().add(line);
            }
        }
        return scenes;
    }

    private static double seconds(final String amount, final String unit) {
        return Double.parseDouble(amount) / ("ms".equals(unit) ? 1000 : 1);
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
