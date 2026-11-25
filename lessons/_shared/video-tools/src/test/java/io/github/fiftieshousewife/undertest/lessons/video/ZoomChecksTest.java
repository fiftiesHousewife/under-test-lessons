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

/* The zoom mistakes caught before anything is recorded. */
class ZoomChecksTest {

    @TempDir
    Path root;

    private Path tape;

    @BeforeEach
    void lesson() throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.createDirectories(root.resolve("lessons/demo/example"));
        Files.writeString(root.resolve("lessons/demo/example/Code.java"), "code\n".repeat(30));
        tape = media.resolve("demo.tape");
    }

    private List<String> problemsIn(final String script) throws IOException {
        Files.writeString(tape, script);
        return ZoomChecks.problems(root, tape).stream().map(Problem::message).toList();
    }

    @Test
    void acceptsOneZoomOnShownLinesWithTimeToRun() throws IOException {
        assertThat(problemsIn("""
                # ---- Scene 1: code ----
                Hide
                Type@1ms `show Code.java 1 10 --zoom 4-6`
                Enter
                Show
                Sleep 6s
                """)).isEmpty();
    }

    @Test
    void refusesAZoomOnLinesThePanelDoesntShow() throws IOException {
        assertThat(problemsIn("""
                # ---- Scene 1: code ----
                Type@1ms `show Code.java 1 10 --zoom 9-12`
                Sleep 6s
                """)).containsExactly("scene 1 zooms on line 11, which its panel doesn't show");
    }

    @Test
    void refusesTwoZoomsInOneSceneAndAZoomWithoutTimeToRun() throws IOException {
        final List<String> problems = problemsIn("""
                # ---- Scene 1: code ----
                Type@1ms `show Code.java 1 10 --zoom 2`
                Type@1ms `show Code.java 11 20 --zoom 12`
                Sleep 3s
                """);

        assertAll(
                () -> assertThat(problems).contains("scene 1 zooms twice; a scene zooms on one place"),
                () -> assertThat(problems).anyMatch(problem -> problem.startsWith("zoom 2 shows for 3.0 s")
                        && problem.contains("needs 4.6 s")));
    }

    @Test
    void leavesScenesWithUntimedLiveRunsToBeCheckedAfterRecording() throws IOException {
        assertThat(problemsIn("""
                # ---- Scene 1: run ----
                Type@1ms `run_live "echo hi"`
                Show
                Wait+Screen /hi/
                Hide
                Type@1ms `show Code.java 1 10 --zoom 4`
                Show
                Sleep 1s
                """)).isEmpty();
    }
}
