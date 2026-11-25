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
 * Checks of a lesson's slides file: it names its sources, its closing slides link the lessons repository by placeholder
 * and only lessons that exist, and what its slides say has no semicolon. {@code SEMICOLON} says what is wrong with
 * spoken text that has one.
 */
final class SlideFileChecks {

    static final String SEMICOLON = "has a semicolon, which the voice runs on through; use a full stop";

    private static final Pattern LESSON_LINK = Pattern.compile("(?m)^- lesson:.*\\(([a-z0-9-]+)\\)\\s*$");

    private SlideFileChecks() {
    }

    static List<Problem> slides(final Path slidesFile) {
        final List<String> lines = lines(slidesFile);
        final String text = String.join("\n", lines);
        final List<Problem> problems = new ArrayList<>(semicolonsSaid(slidesFile, lines));
        if (!text.contains("Sources:")) {
            problems.add(new Problem(slidesFile, "no Sources: line; name where each fact on the slides comes from"));
        }
        if (!text.contains("{lessons-repo}")) {
            problems.add(new Problem(slidesFile, "no {lessons-repo} link on the closing slides"));
        }
        problems.addAll(missingLessons(slidesFile, text));
        return problems;
    }

    private static List<Problem> missingLessons(final Path slidesFile, final String text) {
        final List<Problem> problems = new ArrayList<>();
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

    /* Each line of a slide's Say: text with a semicolon, which the voice runs on through. */
    private static List<Problem> semicolonsSaid(final Path slidesFile, final List<String> lines) {
        final List<Problem> problems = new ArrayList<>();
        boolean saying = false;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            saying = stillSaying(saying, line);
            if (saying && line.contains(";")) {
                problems.add(new Problem(slidesFile, "line " + (i + 1) + ": what the slide says " + SEMICOLON));
            }
        }
        return problems;
    }

    /* True on a slide's Say: line, and on each indented line that carries it on. */
    private static boolean stillSaying(final boolean saying, final String line) {
        return line.startsWith("Say:") || saying && line.startsWith(" ") && !line.isBlank();
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
