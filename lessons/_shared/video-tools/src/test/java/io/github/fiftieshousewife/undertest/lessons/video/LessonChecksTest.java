package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LessonChecksTest {

    @TempDir
    Path folder;

    @Test
    void findsPanelsOutOfRangeShortScenesAndSlidesWithoutSourcesOrLinks() throws IOException {
        // A folder called build above the repository must not hide anything from the checks.
        final Path root = Files.createDirectories(folder.resolve("build/repo"));
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.writeString(media.resolveSibling("Thing.java"), "class Thing {\n}\n");
        final Path tape = Files.writeString(media.resolve("demo.tape"), """
                # ---- Scene 1: the thing ----
                Hide
                Type@1ms `show Thing 1 5 "the thing"`
                Show
                Sleep 2s
                # ---- Scene 2: a missing file ----
                Type@1ms `show Nowhere 1 2`
                Show
                Sleep 30s
                """);
        final Path narration = Files.writeString(media.resolve("demo.narration.md"),
                "1. Here is the thing, which the narration describes at some length before moving on to the next.\n"
                        + "2. Short.\n");
        final Path slides = Files.writeString(media.resolve("demo.slides.md"), "## closing: More\n- https://x.org\n");

        assertAll(
                () -> assertThat(new VideoScriptChecker().check(root)).extracting(Problem::message)
                        .anyMatch(message -> message.contains("asks for lines up to 5")),
                () -> assertThat(LessonChecks.shows(root, tape)).extracting(Problem::message)
                        .anyMatch(message -> message.contains("nothing found for Nowhere")),
                () -> assertThat(LessonChecks.sceneTimes(tape, narration)).extracting(Problem::message)
                        .containsExactly("scene 1 shows for 2 s but its narration needs 11 s; lengthen its last Sleep"),
                () -> assertThat(LessonChecks.slides(slides)).extracting(Problem::message).containsExactly(
                        "no Sources: line; name where each fact on the slides comes from",
                        "no {lessons-repo} link on the closing slides"),
                () -> assertThat(LessonChecks.needed("One two three [pause 1s]")).isBetween(4.4, 4.5));
    }
}
