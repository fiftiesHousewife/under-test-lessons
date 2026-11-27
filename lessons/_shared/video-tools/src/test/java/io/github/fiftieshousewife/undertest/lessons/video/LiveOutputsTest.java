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

/* Tests keeping live commands' output for decks, and the checks that decks show what the narration names. */
class LiveOutputsTest {

    @TempDir
    Path root;

    private Path media;

    @BeforeEach
    void lesson() throws IOException {
        media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.createDirectories(root.resolve("lessons/demo/example"));
        Files.writeString(root.resolve("lessons/demo/example/Code.java"), "code\n".repeat(30));
        Files.writeString(media.resolve("demo.scenes"), """
                ## Scene 1: the code
                show Code.java 1 3 --mark 2
                ## Scene 2: the run
                run_live "echo line 24 failed"
                callout "Look at line 24."
                show Code.java 22 26 --mark 24
                """);
        Files.writeString(media.resolve("demo.narration.md"), "1. Line 2 says two.\n2. The run names line 24.\n");
    }

    /* Keeps an output for the lesson's first live command, as tools/capture-lesson-outputs.py does. */
    private void keep(final String command, final String output) throws IOException {
        final Path outputs = Files.createDirectories(media.resolve("demo.outputs"));
        Files.writeString(outputs.resolve("01.txt"), "$ " + command + "\n" + output + "\n");
    }

    @Test
    void readsWhatALiveCommandPrintedForThatCommandOnly() throws IOException {
        final Media demo = new Media(root, "demo");
        keep("echo line 24 failed", "line 24 failed");

        assertAll(
                () -> assertThat(LiveOutputs.read(demo, 0, "echo line 24 failed")).containsExactly("line 24 failed"),
                () -> assertThat(LiveOutputs.read(demo, 0, "echo something else")).isEmpty(),
                () -> assertThat(LiveOutputs.problems(demo)).isEmpty());
    }

    @Test
    void readsMarkedLinesAndRanges() {
        assertAll(
                () -> assertThat(MarkedLines.marks("18,22-24")).containsExactly(18, 22, 23, 24),
                () -> assertThat(MarkedLines.lineNumber(" 24  code")).isEqualTo(24),
                () -> assertThat(MarkedLines.lineNumber("   \u22ee")).isEqualTo(-1));
    }

    @Test
    void failsKeptOutputThatShowsAPersonalPath() throws IOException {
        final Path outputs = Files.createDirectories(media.resolve("demo.outputs"));
        Files.writeString(outputs.resolve("01.txt"), "$ echo line 24 failed\nsee /Users/someone/repo/build\n");

        assertThat(LiveOutputs.problems(new Media(root, "demo"))).extracting(Problem::message)
                .anyMatch(message -> message.contains("personal path"));
    }

    @Test
    void failsKeptOutputTallerThanOneScreen() throws IOException {
        final Path outputs = Files.createDirectories(media.resolve("demo.outputs"));
        Files.writeString(outputs.resolve("01.txt"), "$ echo line 24 failed\n" + "a test passed\n".repeat(36));

        assertThat(LiveOutputs.problems(new Media(root, "demo"))).extracting(Problem::message)
                .anyMatch(message -> message.contains("prints 37 rows") && message.contains("one screen holds 36"));
    }

    @Test
    void countsTheRowsAnOutputTakesOnScreen() {
        assertAll(
                () -> assertThat(LiveOutputs.rowsOnScreen(List.of("one", "two"))).isEqualTo(3),
                () -> assertThat(LiveOutputs.rowsOnScreen(List.of("x".repeat(151), ""))).isEqualTo(4));
    }

    @Test
    void failsALiveCommandWithoutItsOutput() {
        final List<Problem> problems = LiveOutputs.problems(new Media(root, "demo"));

        assertAll(
                () -> assertThat(problems).hasSize(1),
                () -> assertThat(problems.getFirst().message()).contains("live command 1 (echo line 24 failed)")
                        .contains("capture-lesson-outputs.py demo"));
    }

    @Test
    void failsALineNamedButNotShownMarkedWhereverItIsNamed() throws IOException {
        Files.writeString(media.resolve("demo.scenes"), """
                ## Scene 1: the code
                show Code.java 1 3
                ## Scene 2: the run
                run_live "echo line 7 failed"
                callout "Look at line 24."
                """);
        final Media demo = new Media(root, "demo");
        keep("echo line 7 failed", "line 7 failed");

        final List<String> problems = LiveOutputs.problems(demo).stream().map(Problem::message).toList();

        assertAll(
                () -> assertThat(problems).anyMatch(problem -> problem.startsWith("scene 1 names line 2 "))
                        .anyMatch(problem -> problem.startsWith("scene 2 names line 24 "))
                        .anyMatch(problem -> problem.startsWith("scene 2 names line 7 ")),
                () -> assertThat(problems).allMatch(problem -> problem.contains("--mark")));
    }

    @Test
    void theDeckShowsEachCommandsOutputUnderItAndMarksTheLinesTheSceneIsAbout() throws IOException {
        final Path shared = Files.createDirectories(root.resolve("lessons/_shared/slides/pictures"));
        Files.writeString(shared.resolveSibling("slides.css"), "body { }");
        Files.writeString(shared.resolveSibling("deck.css"), ".deck { }");
        Files.writeString(shared.resolveSibling("deck.js"), "// controls");
        keep("echo line 24 failed", "line 24 failed");

        final String deck = SlideDeck.html(new Media(root, "demo"));

        assertAll(
                () -> assertThat(deck).contains("<pre class=\"command\">$ echo line 24 failed</pre>"
                        + "<pre class=\"output\"><mark>line 24 failed</mark></pre>"),
                () -> assertThat(deck).contains("<mark> 24  code</mark>").doesNotContain("<mark> 23"));
    }
}
