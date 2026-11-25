package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Each live command in a video script waits for its output to show a line ({@code Wait+Screen /pattern/}). A pattern
 * the run no longer prints stalls the recording until it gives up, minutes later, so this checks each pattern against
 * the run's kept output first. "BUILD FAILED" is on screen but never kept, so a wait for it is not checked.
 */
final class LiveWaits {

    private static final Pattern WAIT = Pattern.compile("^Wait\\+Screen(?:@\\S+)?\\s+/(.*)/\\s*$");
    private static final int LINES_TO_A_WAIT = 8;

    private LiveWaits() {
    }

    static List<Problem> problems(final Media media) {
        final Path tape = media.file(".tape");
        if (!Files.isRegularFile(tape)) {
            return List.of();
        }
        final List<String> lines = lines(tape);
        final List<Problem> problems = new ArrayList<>();
        int run = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("run_live")) {
                final String kept = LiveOutputs.fileName(run);
                waitFor(lines, i).flatMap(pattern -> unmatched(media, kept, pattern)).ifPresent(problems::add);
                run++;
            }
        }
        return problems;
    }

    /* The pattern the run's wait looks for, from the first Wait+Screen line after it. */
    private static Optional<String> waitFor(final List<String> lines, final int from) {
        return lines.subList(from + 1, Math.min(lines.size(), from + LINES_TO_A_WAIT)).stream()
                .map(WAIT::matcher).filter(Matcher::matches).map(matcher -> matcher.group(1)).findFirst();
    }

    private static Optional<Problem> unmatched(final Media media, final String file, final String pattern) {
        final Path kept = media.file(LiveOutputs.FOLDER).resolve(file);
        if (pattern.contains("BUILD FAILED") || !Files.isRegularFile(kept)
                || Pattern.compile(pattern).matcher(String.join("\n", lines(kept))).find()) {
            return Optional.empty();
        }
        return Optional.of(new Problem(media.file(".tape"), "a live run waits for /" + pattern + "/, which its kept"
                + " output " + file + " doesn't show; the recording would stall: wait for a line the run prints"));
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException("could not read " + file, e);
        }
    }
}
