package io.github.fiftieshousewife.undertest.lessons.checks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
 * Every link into the toolkit's {@code techniques/} folder, in this repo's Markdown pages, names a technique the
 * toolkit has. Research reviews are skipped: they record what was true when they were written.
 */
final class TechniqueLinks {

    static final Pattern TECHNIQUE_LINK =
            Pattern.compile("under-test/(?:blob|tree)/main/techniques/([a-z0-9-]+)");

    private static final Set<String> SKIPPED = Set.of("build", ".gradle", ".git", ".idea", ".kotlin", "node_modules",
            "apm_modules", "research", ".toolkit");

    private TechniqueLinks() {
    }

    static List<Problem> check(final Path root, final Set<String> techniqueIds) {
        final List<Problem> problems = new ArrayList<>();
        for (final Path page : pages(root)) {
            for (final String id : Text.matches(TECHNIQUE_LINK, Text.read(page))) {
                if (!techniqueIds.contains(id)) {
                    problems.add(new Problem(page, "links to the toolkit technique " + id + ", which it doesn't have"));
                }
            }
        }
        return problems;
    }

    private static List<Path> pages(final Path root) {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(path -> path.toString().endsWith(".md"))
                    .filter(path -> notSkipped(root.relativize(path)))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static boolean notSkipped(final Path relative) {
        for (final Path part : relative) {
            if (SKIPPED.contains(part.toString())) {
                return false;
            }
        }
        return true;
    }
}
