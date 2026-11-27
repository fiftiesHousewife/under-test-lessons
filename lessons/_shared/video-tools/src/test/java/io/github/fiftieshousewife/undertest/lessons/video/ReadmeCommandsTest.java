package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* A lesson's README states what each command shows; the check holds it to what the command really printed. */
class ReadmeCommandsTest {

    @TempDir
    Path root;

    private Path lessonFolder;

    @BeforeEach
    void lesson() throws IOException {
        lessonFolder = Files.createDirectories(root.resolve("lessons/demo"));
        final Path media = Files.createDirectories(lessonFolder.resolve("media"));
        Files.writeString(media.resolve("demo.scenes"), """
                ## Scene 1: weak
                run_live "./gradlew :lessons:demo:test -Pcode=today"
                ## Scene 2: strong
                run_live "./gradlew :lessons:demo:test"
                """);
        final Path outputs = Files.createDirectories(media.resolve("demo.outputs"));
        Files.writeString(outputs.resolve("01.txt"), "$ ./gradlew :lessons:demo:test -Pcode=today\n"
                + "ATest > one() FAILED\n1 of 13 tests failed, 5 of 13 planted bugs caught\n");
        Files.writeString(outputs.resolve("02.txt"), "$ ./gradlew :lessons:demo:test\nAll 13 tests passed.\n");
    }

    private List<String> problems(final String readme) throws IOException {
        Files.writeString(lessonFolder.resolve("README.md"), readme);
        return ReadmeCommands.problems(new Media(root, "demo")).stream().map(Problem::message).toList();
    }

    @Test
    void acceptsResultsTheOutputsBearOut() throws IOException {
        assertThat(problems("""
                ```bash
                ./gradlew :lessons:demo:test -Pcode=today   # today's code: only one test fails; 5 of 13 caught
                ./gradlew :lessons:demo:test                # the fixed code: every test passes, all 13
                ```
                """)).isEmpty();
    }

    @Test
    void refusesResultsTheOutputsContradict() throws IOException {
        final List<String> problems = problems("""
                ```bash
                ./gradlew :lessons:demo:test -Pcode=today   # today's code: 6 of 13 caught
                ./gradlew :lessons:demo:test                # the fixed code: only the slow test fails
                ```
                """);

        assertAll(
                () -> assertThat(problems).anyMatch(p -> p.contains("says 6") && p.contains("-Pcode=today")),
                () -> assertThat(problems).anyMatch(p -> p.contains("says only one test fails")));
    }

    @Test
    void refusesACommandNoSceneRunsAndSaysPassesWhenOneFailed() throws IOException {
        final List<String> problems = problems("""
                ```bash
                ./gradlew :lessons:demo:check               # everything
                ./gradlew :lessons:demo:test -Pcode=today   # today's code: every test passes
                ```
                """);

        assertAll(
                () -> assertThat(problems).anyMatch(p -> p.contains(":lessons:demo:check")
                        && p.contains("isn't one of the deck's live commands")),
                () -> assertThat(problems).anyMatch(p -> p.contains("says every test passes")));
    }

    @Test
    void holdsTheExerciseAnswerToItsOwnKeptOutput() throws IOException {
        final String readme = """
                ```bash
                ./gradlew :lessons:demo:test -Pcode=answer   # the answer: 9 of 13 caught
                ```
                """;
        final List<String> withoutOutput = problems(readme);
        Files.writeString(lessonFolder.resolve("media/demo.outputs/answer.txt"),
                "$ ./gradlew :lessons:demo:test -Pcode=answer\n9 of 13 planted bugs caught\n");
        final List<String> bornOut = problems(readme);
        final List<String> contradicted = problems(readme.replace("9 of 13", "10 of 13"));

        assertAll(
                () -> assertThat(withoutOutput).singleElement().asString().contains("has no kept output"),
                () -> assertThat(bornOut).isEmpty(),
                () -> assertThat(contradicted).singleElement().asString().contains("says 10"));
    }
}
