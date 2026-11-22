package io.github.fiftieshousewife.undertest.lessons.checks;

import static io.github.fiftieshousewife.undertest.lessons.checks.TestRepo.lessonLink;
import static io.github.fiftieshousewife.undertest.lessons.checks.TestRepo.techniqueLink;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LessonTablesTest {

    private static final List<Lesson> LESSONS = List.of(
            new Lesson("real-databases", "Real databases", List.of("testcontainers")),
            new Lesson("liquibase", "Liquibase", List.of("testcontainers")),
            new Lesson("nested-tests", "Nested tests", List.of()));

    @TempDir
    Path root;

    private List<String> catalogueMessages(final Optional<Set<String>> toolkit, final String... rows) {
        new TestRepo(root).catalogue(rows);
        return LessonTables.catalogue(root, LESSONS, toolkit).stream().map(Problem::message).toList();
    }

    @Test
    void acceptsACatalogueThatMatchesTheLessonsAndTheToolkit() {
        assertThat(catalogueMessages(Optional.of(Set.of("testcontainers", "contract-testing")),
                "testcontainers|" + lessonLink("real-databases") + ", " + lessonLink("liquibase"),
                "contract-testing|Contract tests between teams")).isEmpty();
    }

    @Test
    void reportsARowThatLeavesOutALessonThatNamesItsTechnique() {
        assertThat(catalogueMessages(Optional.empty(), "testcontainers|" + lessonLink("real-databases")))
                .singleElement().asString().contains("testcontainers: the row links [real-databases]");
    }

    @Test
    void reportsMissingAndUnknownRowsAgainstTheToolkit() {
        final List<String> messages = catalogueMessages(Optional.of(Set.of("testcontainers", "feature-flags")),
                "testcontainers|" + lessonLink("real-databases") + ", " + lessonLink("liquibase"),
                "snapshot-testing|Comparing structured output");

        assertThat(messages).containsExactlyInAnyOrder("no row for the technique feature-flags",
                "a row for snapshot-testing, which the toolkit doesn't have");
    }

    @Test
    void reportsAMissingRowForATechniqueThatALessonNames() {
        assertThat(catalogueMessages(Optional.empty())).containsExactly("no row for the technique testcontainers");
    }

    @Test
    void checksTheLessonsIndexAgainstEachLesson() {
        new TestRepo(root).index(
                lessonLink("real-databases") + "|" + techniqueLink("testcontainers"),
                lessonLink("liquibase") + "|" + techniqueLink("fake-behind-an-interface"));

        final List<String> messages = LessonTables.index(root, LESSONS).stream().map(Problem::message).toList();

        assertAll(
                () -> assertThat(messages).hasSize(2),
                () -> assertThat(messages).anyMatch(message -> message.startsWith("liquibase: the row links")),
                () -> assertThat(messages).contains("no row for the lesson nested-tests"));
    }

    @Test
    void refusesAPageWithoutTheHeading() {
        TestRepo.write(root, LessonTables.INDEX_PAGE, "# Lessons\n");

        assertThatThrownBy(() -> LessonTables.index(root, LESSONS)).hasMessageContaining("has no heading");
    }
}
