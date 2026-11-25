package io.github.fiftieshousewife.undertest.lessons.checks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/* A small lessons repo and toolkit checkout in a temporary folder, built up file by file. */
record TestRepo(Path root) {

    TestRepo {
        write(root, LessonReader.SCHEMA, Text.read(Path.of(System.getProperty("lessonSchema"))));
    }

    static void write(final Path root, final String relative, final String text) {
        try {
            final Path file = root.resolve(relative);
            Files.createDirectories(Objects.requireNonNull(file.getParent()));
            Files.writeString(file, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /* A lesson folder with a README and a lesson.yaml naming the given techniques (comma-separated). */
    TestRepo lesson(final String id, final String techniques) {
        write(root, "lessons/" + id + "/README.md", "# " + id + "\n");
        write(root, "lessons/" + id + "/lesson.yaml",
                "# yaml-language-server: $schema=../../schema/lesson.schema.json\n"
                        + "id: " + id + "\ntitle: A lesson\ntechniques: [" + techniques + "]\n");
        return this;
    }

    /* The catalogue's technique table, one row per {@code technique|lesson cell}. */
    TestRepo catalogue(final String... rows) {
        write(root, LessonTables.CATALOGUE_PAGE,
                table(LessonTables.CATALOGUE_HEADING, "| Technique | Lesson | When |", rows));
        return this;
    }

    /* The lessons index table, one row per {@code lesson cell|technique cell}. */
    TestRepo index(final String... rows) {
        write(root, LessonTables.INDEX_PAGE, table(LessonTables.INDEX_HEADING, "| Lesson | Technique | Deck |", rows));
        return this;
    }

    /* A toolkit checkout inside the repo, where CI puts it, with the given technique folders. */
    Path toolkit(final String... techniques) {
        final Path toolkit = root.resolve(".toolkit");
        for (final String technique : techniques) {
            write(toolkit, "techniques/" + technique + "/technique.yaml", "id: " + technique + "\n");
        }
        return toolkit;
    }

    static String lessonLink(final String id) {
        return "[" + id + "](" + id + "/README.md)";
    }

    static String techniqueLink(final String id) {
        return "[" + id + "](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/" + id
                + "/technique.yaml)";
    }

    private static String table(final String heading, final String header, final String... rows) {
        final StringBuilder text = new StringBuilder("# Page\n\n" + heading + "\n\n" + header + "\n|---|---|---|\n");
        for (final String row : rows) {
            text.append("| ").append(row.replace("|", " | ")).append(" | x |\n");
        }
        return text.append("\n## Next section\n\n| Not | Read |\n|---|---|\n| a | b |\n").toString();
    }
}
