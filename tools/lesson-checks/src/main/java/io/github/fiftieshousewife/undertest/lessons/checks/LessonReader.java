package io.github.fiftieshousewife.undertest.lessons.checks;

import com.networknt.schema.Error;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import tools.jackson.databind.JsonNode;
import tools.jackson.dataformat.yaml.YAMLMapper;

/**
 * Reads {@code lessons/<id>/lesson.yaml} for every lesson folder (a folder under {@code lessons/} with a README, other
 * than the shared ones starting with an underscore), checks each against {@code schema/lesson.schema.json}, and checks
 * that its id is its folder name.
 */
public final class LessonReader {

    static final String SCHEMA = "schema/lesson.schema.json";

    private final YAMLMapper yaml = new YAMLMapper();
    private final SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);

    /** The lessons that could be read; anything wrong is added to {@code problems}. */
    public List<Lesson> read(final Path root, final List<Problem> problems) {
        final Schema schema = registry.getSchema(Text.read(root.resolve(SCHEMA)));
        final List<Lesson> lessons = new ArrayList<>();
        for (final Path folder : lessonFolders(root)) {
            if (Files.isRegularFile(folder.resolve("lesson.yaml"))) {
                readOne(folder, schema, problems).ifPresent(lessons::add);
            } else {
                problems.add(new Problem(folder,
                        "no lesson.yaml: every lesson names the toolkit techniques it teaches"));
            }
        }
        return lessons;
    }

    static List<Path> lessonFolders(final Path root) {
        try (Stream<Path> paths = Files.list(root.resolve("lessons"))) {
            return paths.filter(path -> Files.isRegularFile(path.resolve("README.md")))
                    .filter(path -> !String.valueOf(path.getFileName()).startsWith("_"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Optional<Lesson> readOne(final Path folder, final Schema schema, final List<Problem> problems) {
        final Path file = folder.resolve("lesson.yaml");
        final JsonNode document = yaml.readTree(Text.read(file));
        final List<Error> errors = schema.validate(document);
        if (!errors.isEmpty()) {
            errors.forEach(error -> problems.add(new Problem(file, error.toString())));
            return Optional.empty();
        }
        final Lesson lesson = yaml.treeToValue(document, Lesson.class);
        final String folderName = String.valueOf(folder.getFileName());
        if (!lesson.id().equals(folderName)) {
            problems.add(new Problem(file, "id " + lesson.id() + " is not the folder name " + folderName));
        }
        return Optional.of(lesson);
    }
}
