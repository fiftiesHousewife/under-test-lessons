package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
 * Phrases the narration never uses, from lessons/_shared/dont-say.txt: each line a regular expression, then " = " and
 * why. Every slide's Say text and every scene's narration is checked, so a rule agreed once holds in every
 * lesson.
 */
final class DontSay {

    static final String LIST = "lessons/_shared/dont-say.txt";

    private DontSay() {
    }

    /* A phrase to avoid, and why. */
    record Rule(Pattern phrase, String why) {
    }

    static List<Problem> problems(final Media media) {
        final Path list = media.root().resolve(LIST);
        if (!Files.isRegularFile(list)) {
            return List.of();
        }
        final List<Rule> rules = rules(lines(list));
        final List<Problem> problems = new ArrayList<>();
        final Path slides = media.file(".slides.md");
        spoken(media).forEach(text -> rules.stream().filter(rule -> rule.phrase().matcher(text).find())
                .forEach(rule -> problems.add(new Problem(slides, "the narration says /" + rule.phrase().pattern()
                        + "/: " + rule.why() + " (\"" + excerpt(text, rule.phrase()) + "\")"))));
        return problems;
    }

    static List<Rule> rules(final List<String> lines) {
        return lines.stream().filter(line -> !line.isBlank() && !line.startsWith("#") && line.contains(" = "))
                .map(line -> new Rule(Pattern.compile(line.substring(0, line.indexOf(" = ")).strip()),
                        line.substring(line.indexOf(" = ") + 3).strip()))
                .toList();
    }

    /* Everything the voice says in the lesson: the slides' Say text and the scenes' narration. */
    private static List<String> spoken(final Media media) {
        final Path narration = media.file(".narration.md");
        final Stream<String> scenes = Files.isRegularFile(narration)
                ? Narration.paragraphs(narration).stream() : Stream.empty();
        final Stream<String> slides = Files.isRegularFile(media.file(".slides.md"))
                ? Stream.concat(media.slides().intro().stream(), media.slides().closing().stream())
                        .map(Slide::narration)
                : Stream.empty();
        return Stream.concat(slides, scenes).toList();
    }

    private static String excerpt(final String text, final Pattern phrase) {
        final Matcher found = phrase.matcher(text);
        if (!found.find()) {
            return "";
        }
        return text.substring(Math.max(0, found.start() - 30), Math.min(text.length(), found.end() + 30)).strip();
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException("could not read " + file, e);
        }
    }
}
