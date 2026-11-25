package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LessonChecksTest {

    @TempDir
    Path folder;

    // A folder called build above the repository must not hide anything from the checks.
    private Path root() {
        return folder.resolve("build/repo");
    }

    private Path media() {
        return root().resolve("lessons/demo/media");
    }

    @BeforeEach
    void writeALesson() throws IOException {
        final Path media = Files.createDirectories(media());
        Files.writeString(media.resolveSibling("Thing.java"), "class Thing {\n}\n");
        Files.writeString(media.resolve("demo.tape"), """
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
        Files.writeString(media.resolve("demo.narration.md"),
                "1. Here is the thing, which the narration describes at some length before moving on to the next.\n"
                        + "2. Short.\n");
        Files.writeString(media.resolve("demo.slides.md"), "## closing: More\n- https://x.org\n");
    }

    @Test
    void findsPanelsOutOfRangeShortScenesAndSlidesWithoutSourcesOrLinks() {
        final Path root = root();
        final Path tape = media().resolve("demo.tape");
        final Path narration = media().resolve("demo.narration.md");
        final Path slides = media().resolve("demo.slides.md");

        assertAll(
                () -> assertThat(new VideoScriptChecker().check(root)).extracting(Problem::message)
                        .anyMatch(message -> message.contains("asks for lines up to 5")),
                () -> assertThat(LessonChecks.shows(root, tape)).extracting(Problem::message)
                        .anyMatch(message -> message.contains("nothing found for Nowhere")),
                () -> assertThat(LessonChecks.sceneTimes(tape, narration)).extracting(Problem::message)
                        .containsExactly("scene 1 shows for 2 s but its narration needs 11 s; lengthen its last Sleep"),
                () -> assertThat(SlideFileChecks.slides(slides)).extracting(Problem::message).containsExactly(
                        "no Sources: line; name where each fact on the slides comes from",
                        "no {lessons-repo} link on the closing slides"),
                () -> assertThat(LessonChecks.needed("One two three [pause 1s]")).isBetween(4.4, 4.5));
    }
}
