package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Checks that catch what reviews found by hand: code panels that would show the wrong file or lines, scenes too short
 * for their narration, and slides without sources or repository links. A scene needs its narration at
 * {@code WORDS_PER_SECOND} spoken words a second, times {@code ROOM}, and {@code EXTRA_SECONDS} more.
 */
final class LessonChecks {

    static final double WORDS_PER_SECOND = 2.6;
    static final double ROOM = 1.35;
    static final double EXTRA_SECONDS = 1.5;

    private static final Pattern SCENE = Pattern.compile("^#\\s*-+\\s*Scene\\s+\\d+:.*");
    private static final Pattern TYPED = Pattern.compile("^Type(?:@\\S+)?\\s+([\"`])(.*)\\1\\s*$");
    private static final Pattern SHOWN_FOR = Pattern.compile("^Show\\s*$");
    private static final Pattern SLEEP = Pattern.compile("^Sleep\\s+(\\d+(?:\\.\\d+)?)(ms|s)\\s*$");
    private static final Pattern PAUSE = Pattern.compile("\\[pause\\s+(\\d+(?:\\.\\d+)?)(ms|s)]");

    private LessonChecks() {
    }

    /* Each {@code show} line finds a file (in its lesson first) and any line range fits inside it. */
    static List<Problem> shows(final Path root, final Path tape) {
        final List<Path> places = ShownCode.places(root, tape);
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
            file = ShownCode.find(root, places, words.get(1));
        } catch (IllegalArgumentException e) {
            return List.of(new Problem(root, e.getMessage()));
        }
        if (asksForLines(words)) {
            final int length = lines(file).size();
            if (Integer.parseInt(words.get(3)) > length) {
                return List.of(new Problem(root, "show " + words.get(1) + " asks for lines up to " + words.get(3)
                        + ", but " + file.getFileName() + " has " + length));
            }
        }
        return List.of();
    }

    private static boolean asksForLines(final List<String> words) {
        return words.size() > 3 && words.get(2).matches("\\d+") && words.get(3).matches("\\d+");
    }

    /* Each scene without a live run shows for at least its narration's spoken length, with room. */
    static List<Problem> sceneTimes(final Path tape, final Path narration) {
        final List<List<String>> scenes = scenes(lines(tape));
        final List<String> paragraphs = Narration.rawParagraphs(narration);
        final List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < Math.min(scenes.size(), paragraphs.size()); i++) {
            final List<String> scene = scenes.get(i);
            if (!shownWithoutALiveRun(scene)) {
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

    private static boolean shownWithoutALiveRun(final List<String> scene) {
        final boolean live = scene.stream()
                .anyMatch(line -> line.contains("run_live") || line.contains("run_with_notes"));
        return !live && scene.stream().anyMatch(line -> SHOWN_FOR.matcher(line).matches());
    }

    static double needed(final String paragraph) {
        return spoken(paragraph) * ROOM + EXTRA_SECONDS;
    }

    /* Seconds a paragraph takes to say: its words, and its pauses; needed adds the room a scene gives it on screen. */
    static double spoken(final String paragraph) {
        double pauses = 0;
        final Matcher pause = PAUSE.matcher(paragraph);
        while (pause.find()) {
            pauses += seconds(pause.group(1), pause.group(2));
        }
        final String[] words = PAUSE.matcher(paragraph).replaceAll(" ").strip().split("\\s+");
        return words.length / WORDS_PER_SECOND + pauses;
    }

    private static double lastShownFor(final List<String> scene) {
        double shown = 0;
        boolean afterShow = false;
        for (final String line : scene) {
            final boolean show = SHOWN_FOR.matcher(line).matches();
            if (afterShow && !show) {
                shown = sleepSeconds(line, shown);
            }
            afterShow = show;
        }
        return shown;
    }

    /* The seconds a {@code Sleep} line waits, or {@code otherwise} for any other line. */
    private static double sleepSeconds(final String line, final double otherwise) {
        final Matcher sleep = SLEEP.matcher(line);
        return sleep.matches() ? seconds(sleep.group(1), sleep.group(2)) : otherwise;
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
