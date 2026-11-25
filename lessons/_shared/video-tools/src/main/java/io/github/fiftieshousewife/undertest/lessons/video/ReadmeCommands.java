package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Holds a lesson's README to what its commands really print. Each {@code ./gradlew :lessons:...} line in the README
 * must be one of the video's live commands, whose output is kept ({@link LiveOutputs}); and what its comment says must
 * match that output: every number it gives appears there, "only ... fails" means exactly one test failed, "fails" that
 * one did, and "every ... passes" (or holds, matches) that none did. A lesson's exercise answer ({@code -Pcode=answer})
 * isn't in the video; its output is kept beside the live ones and checked the same way. CI keeps the outputs afresh
 * before checking, so there the commands really run.
 */
final class ReadmeCommands {

    /* How a README runs a lesson's exercise answer. */
    static final String ANSWER_SWITCH = "-Pcode=answer";
    private static final Pattern COMMAND = Pattern.compile("^(\\./gradlew :lessons:[^#]*?)\\s*(?:#\\s*(.*))?$");
    private static final Pattern NUMBER = Pattern.compile("\\b\\d+\\b");
    private static final Pattern FAILED_TEST = Pattern.compile("^[A-Za-z].* > .* FAILED$");
    private static final Pattern ALL_PASS = Pattern.compile("\\b(every|all)\\b.*\\b(pass|passes|holds|matches)\\b");

    private ReadmeCommands() {
    }

    /* What the README says that its commands' outputs don't bear out. */
    static List<Problem> problems(final Media media) {
        final Path readme = media.folder().resolveSibling("README.md");
        if (!Files.isRegularFile(readme) || !Files.isRegularFile(media.file(".tape"))) {
            return List.of();
        }
        final List<String> live = LiveOutputs.commands(SceneContent.read(media.root(), media.file(".tape")));
        final List<Problem> problems = new ArrayList<>();
        for (final String line : lines(readme)) {
            final Matcher command = COMMAND.matcher(line.strip());
            if (command.matches()) {
                final String says = command.group(2) == null ? "" : command.group(2);
                final String typed = command.group(1).replaceAll("\\s+", " ");
                problems.addAll(checked(media, live, new Claim(readme, typed, says)));
            }
        }
        return problems;
    }

    /* What is wrong with one README command and what it says: a live command, or the exercise's answer. */
    private static List<Problem> checked(final Media media, final List<String> live, final Claim claim) {
        final int index = live.indexOf(claim.command());
        if (index < 0 && claim.command().endsWith(ANSWER_SWITCH)) {
            return answer(media, claim);
        }
        if (index < 0) {
            return List.of(new Problem(claim.readme(), "`" + claim.command() + "` isn't one of the video's live"
                    + " commands, so what the README says it shows can't be checked; run it in the video, or leave it"
                    + " out"));
        }
        final List<String> output = LiveOutputs.read(media, index, claim.command());
        return claim.says().isEmpty() || output.isEmpty() ? List.of() : contradictions(claim, output);
    }

    /*
     * The exercise's answer command, which no video runs: its output is kept as {@code answer.txt} with the live ones
     * ({@link LiveOutputs#readAnswer}), and what the README says about it is held to that.
     */
    private static List<Problem> answer(final Media media, final Claim claim) {
        final List<String> output = LiveOutputs.readAnswer(media, claim.command());
        if (output.isEmpty()) {
            return List.of(new Problem(claim.readme(), "`" + claim.command() + "` has no kept output; run python3"
                    + " tools/capture-lesson-outputs.py " + media.safeLesson()));
        }
        return contradictions(claim, output);
    }

    /* A README line's command and what its comment says it shows. */
    private record Claim(Path readme, String command, String says) {
    }

    private static List<Problem> contradictions(final Claim claim, final List<String> output) {
        final List<Problem> problems = new ArrayList<>(numbers(claim, String.join("\n", output)));
        failures(claim, output.stream().filter(line -> FAILED_TEST.matcher(line.strip()).matches()).count())
                .ifPresent(problems::add);
        return problems;
    }

    /* Every number the comment gives appears in the output. */
    private static List<Problem> numbers(final Claim claim, final String printed) {
        final List<Problem> problems = new ArrayList<>();
        final Matcher number = NUMBER.matcher(claim.says());
        while (number.find()) {
            if (!Pattern.compile("\\b" + number.group() + "\\b").matcher(printed).find()) {
                problems.add(new Problem(claim.readme(), "the README says " + number.group() + " for `"
                        + claim.command() + "`, but its output doesn't show " + number.group()));
            }
        }
        return problems;
    }

    /* What the comment says failed or passed matches how many tests failed. */
    private static java.util.Optional<Problem> failures(final Claim claim, final long failed) {
        final String words = claim.says().toLowerCase(Locale.ROOT);
        final boolean only = words.contains("only") && words.contains("fail");
        final String expected;
        if (only && failed != 1) {
            expected = "only one test fails";
        } else if (!only && words.contains("fail") && failed == 0) {
            expected = "a test fails";
        } else if (ALL_PASS.matcher(words).find() && failed > 0) {
            expected = "every test passes";
        } else {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new Problem(claim.readme(), "the README says " + expected + " for `"
                + claim.command() + "`, but " + failed + " failed in its output"));
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
