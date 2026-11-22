package io.github.fiftieshousewife.undertest.lessons.checks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LessonReaderTest {

    @TempDir
    Path root;

    @Test
    void readsEachLessonAndSkipsSharedFolders() {
        new TestRepo(root).lesson("mutation-testing", "mutation-testing").lesson("nested-tests", "");
        TestRepo.write(root, "lessons/_shared/README.md", "# Shared\n");
        final List<Problem> problems = new ArrayList<>();

        final List<Lesson> lessons = new LessonReader().read(root, problems);

        assertAll(
                () -> assertThat(problems).isEmpty(),
                () -> assertThat(lessons).extracting(Lesson::id).containsExactly("mutation-testing", "nested-tests"),
                () -> assertThat(lessons.getFirst().techniques()).containsExactly("mutation-testing"));
    }

    @Test
    void reportsALessonWithoutALessonFile() {
        new TestRepo(root);
        TestRepo.write(root, "lessons/new-lesson/README.md", "# New\n");
        final List<Problem> problems = new ArrayList<>();

        new LessonReader().read(root, problems);

        assertThat(problems).singleElement().extracting(Problem::message).asString().contains("no lesson.yaml");
    }

    @Test
    void reportsAFileThatBreaksTheSchemaOrNamesTheWrongFolder() {
        new TestRepo(root).lesson("right-folder", "");
        TestRepo.write(root, "lessons/right-folder/lesson.yaml", "id: wrong-folder\ntitle: A lesson\ntechniques: []\n");
        TestRepo.write(root, "lessons/broken/README.md", "# Broken\n");
        TestRepo.write(root, "lessons/broken/lesson.yaml", "id: broken\ncolour: red\n");
        final List<Problem> problems = new ArrayList<>();

        final List<Lesson> lessons = new LessonReader().read(root, problems);

        assertAll(
                () -> assertThat(lessons).extracting(Lesson::id).containsExactly("wrong-folder"),
                () -> assertThat(problems).extracting(Problem::message)
                        .anyMatch(message -> message.contains("is not the folder name right-folder"))
                        .anyMatch(message -> message.contains("colour")));
    }
}
