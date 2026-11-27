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
        Files.writeString(media.resolve("demo.scenes"), """
                ## Scene 1: the thing
                show Thing 1 5 "the thing"

                ## Scene 2: a missing file
                show Nowhere 1 2
                """);
        Files.writeString(media.resolve("demo.narration.md"),
                "1. Here is the thing, which the narration describes at some length before moving on to the next.\n"
                        + "2. Short.\n");
        Files.writeString(media.resolve("demo.slides.md"), "## closing: More\n- https://x.org\n");
    }

    @Test
    void findsPanelsOutOfRangeAndSlidesWithoutSourcesOrLinks() {
        final Path root = root();
        final Path scenes = media().resolve("demo.scenes");
        final Path slides = media().resolve("demo.slides.md");

        assertAll(
                () -> assertThat(new SceneFileChecker().check(root)).extracting(Problem::message)
                        .anyMatch(message -> message.contains("asks for lines up to 5")),
                () -> assertThat(LessonChecks.shows(root, scenes)).extracting(Problem::message)
                        .anyMatch(message -> message.contains("nothing found for Nowhere")),
                () -> assertThat(SlideFileChecks.slides(slides)).extracting(Problem::message).containsExactly(
                        "no Sources: line; name where each fact on the slides comes from",
                        "no {lessons-repo} link on the closing slides"),
                () -> assertThat(Pauses.spokenSeconds("One two three [pause 1s]")).isBetween(2.1, 2.2));
    }
}
