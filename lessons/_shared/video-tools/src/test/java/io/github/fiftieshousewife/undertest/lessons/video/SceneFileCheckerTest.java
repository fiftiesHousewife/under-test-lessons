package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SceneFileCheckerTest {

    @TempDir
    Path root;

    private Path media() throws IOException {
        return Files.createDirectories(root.resolve("lessons/example-lesson/media"));
    }

    private List<String> problemsIn(final String scenes, final String narration) throws IOException {
        final Path media = media();
        Files.writeString(media.resolve("example-lesson.scenes"), scenes);
        Files.writeString(media.resolve("example-lesson.narration.md"), narration);
        return new SceneFileChecker().check(root).stream().map(Problem::message).toList();
    }

    @Test
    void acceptsAWellFormedSceneFile() throws IOException {
        Files.createDirectories(root.resolve("lessons/example-lesson/example"));
        Files.writeString(root.resolve("lessons/example-lesson/example/Thing.java"), "class Thing {\n}\n");
        final String scenes = """
                # The scenes of the example deck.

                ## Scene 1: the thing
                note "The thing, as it is."
                show Thing 1 2 "the thing"
                """;

        assertThat(problemsIn(scenes, "1. Here is the thing.\n")).isEmpty();
    }

    @Test
    void reportsNarrationThatWouldReadACommandAloud() throws IOException {
        assertThat(problemsIn("## Scene 1: a\n\n## Scene 2: b\n",
                "1. Now we run the weak tests.\n2. Run `./gradlew pitest` now.\n"))
                .containsExactly("item 2 contains a command, which the voice would read aloud; say what happens instead"
                        + " (for example, \"Now we run the strong tests\")");
    }

    @Test
    void reportsSpokenTextWithASemicolon() throws IOException {
        Files.writeString(media().resolve("example-lesson.slides.md"), "Sources: a; b\n\n## intro: One\n- A; B\n"
                + "Say: Here is one.\n  It passes; nobody notices.\n\n## closing: More\n- {lessons-repo}\n"
                + "Say: Goodbye.\n");

        assertThat(problemsIn("## Scene 1: a\n", "1. It passes; nobody notices.\n")).containsExactly(
                "item 1 has a semicolon, which the voice runs on through; use a full stop",
                "line 6: what the slide says has a semicolon, which the voice runs on through; use a full stop");
    }

    @Test
    void checksTheSlidesOfALessonWithoutScenes() throws IOException {
        Files.writeString(media().resolve("example-lesson.slides.md"), "## closing: More\n- https://x.org\n");

        assertThat(new SceneFileChecker().check(root)).extracting(Problem::message).containsExactly(
                "no Sources: line; name where each fact on the slides comes from",
                "no {lessons-repo} link on the closing slides");
    }

    @Test
    void findsNoLessonsWhenThereIsNoLessonsFolder() {
        assertThat(new SceneFileChecker().check(root)).isEmpty();
    }

    @Test
    void mainPassesOnGoodScenesAndFailsOnBadOnes() throws IOException {
        problemsIn("## Scene 1: a\n", "1. One.\n");
        DeckChecks.main(root.toString());
        problemsIn("## Scene 1: a\n", "1. One; two.\n2. Three.\n");

        assertAll(
                () -> assertThatThrownBy(() -> DeckChecks.main(root.toString()))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("2 problem(s)"),
                () -> assertThatThrownBy(DeckChecks::main).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(new Problem(root, "x").toString()).endsWith(": x"));
    }

    @Test
    void reportsAScriptWhoseScenesAndNarrationItemsDiffer() throws IOException {
        final Path scenes = Files.writeString(root.resolve("x.scenes"),
                "# a comment\n## Scene 1: one\nnote \"a\"\n## Scene 1: one\n\n## Scene 2: two\n");
        final Path narration = Files.writeString(root.resolve("x.narration.md"), "1. One.\n2. Two.\n");
        final Path matching = Files.writeString(root.resolve("y.narration.md"), "1. One.\n2. Two.\n3. Three.\n");

        assertAll(
                () -> assertThat(Narration.checkSceneCount(scenes, narration)).get()
                        .extracting(Object::toString).asString().contains("3 scenes but 2 narration items"),
                () -> assertThat(Narration.checkSceneCount(scenes, matching)).isEmpty(),
                () -> assertThat(SceneContent.titles(scenes)).containsExactly("one", "one", "two"));
    }
}
