package io.github.fiftieshousewife.undertest.lessons.checks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A checkout of the toolkit (under-test). Its technique ids are the folders under {@code techniques/} that hold a
 * {@code technique.yaml}; its skills, the steps of its journey, are the folders under {@code .github/skills/} that hold
 * a {@code SKILL.md}.
 */
public record Toolkit(Path root) {

    public Set<String> techniqueIds() {
        final Path techniques = root.resolve("techniques");
        if (!Files.isDirectory(techniques)) {
            throw new IllegalArgumentException("not a toolkit checkout (no techniques/ folder): " + root);
        }
        try (Stream<Path> folders = Files.list(techniques)) {
            return folders.filter(folder -> Files.isRegularFile(folder.resolve("technique.yaml")))
                    .map(folder -> String.valueOf(folder.getFileName()))
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Set<String> skillNames() {
        final Path skills = root.resolve(".github/skills");
        if (!Files.isDirectory(skills)) {
            return Set.of();
        }
        try (Stream<Path> folders = Files.list(skills)) {
            return folders.filter(folder -> Files.isRegularFile(folder.resolve("SKILL.md")))
                    .map(folder -> String.valueOf(folder.getFileName()))
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
