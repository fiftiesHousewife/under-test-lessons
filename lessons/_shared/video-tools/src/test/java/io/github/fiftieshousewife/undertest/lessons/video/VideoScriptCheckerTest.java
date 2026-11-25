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

class VideoScriptCheckerTest {

    @TempDir
    Path root;

    private List<String> problemsIn(final String script) throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/example-lesson/media"));
        Files.writeString(media.resolve("example-lesson.tape"), script);
        return new VideoScriptChecker().check(root).stream().map(Problem::message).toList();
    }

    @Test
    void acceptsAWellFormedScript() throws IOException {
        Files.createDirectories(root.resolve("lessons/_shared"));
        Files.writeString(root.resolve("lessons/_shared/setup.tape"), "Hide\n");
        final String script = """
                Source lessons/_shared/setup.tape
                Type "echo SETUP-$((1+1))-DONE"
                Wait+Screen@600s /SETUP-2-DONE/
                Type `./gradlew -q :lessons:x:pitest --rerun 2>&1 | grep -E 'Killed [0-9]+'`
                Wait+Screen@300s /Killed \\d+/
                """;

        assertThat(problemsIn(script)).isEmpty();
    }

    @Test
    void reportsEachKnownMistakeWithItsLine() throws IOException {
        final String script = """
                Source lessons/_shared/missing.tape
                Type "echo DONE"
                Wait@60s /DONE/
                Wait
                Type "./gradlew -q :lessons:x:pitest 2>&1 | grep Killed"
                Wait+Screen /Killed (/
                """;

        final List<String> problems = problemsIn(script);

        assertAll(
                () -> assertThat(problems).anyMatch(p -> p.startsWith("line 1: Source file not found")),
                () -> assertThat(problems).anyMatch(p -> p.startsWith("line 3: Wait looks only at the last line")),
                () -> assertThat(problems).anyMatch(p -> p.startsWith("line 3: Wait pattern already matches")),
                () -> assertThat(problems).anyMatch(p -> p.startsWith("line 4: Wait has no pattern")),
                () -> assertThat(problems).anyMatch(p -> p.startsWith("line 5: Gradle output piped into grep")),
                () -> assertThat(problems).anyMatch(p -> p.startsWith("line 6: Wait pattern is not a valid")),
                () -> assertThat(problems).hasSize(7));
    }

    @Test
    void reportsTypingBeforeThePreviousCommandHasFinished() throws IOException {
        final String script = """
                Type "first"
                Enter
                Type "second"
                Enter
                Sleep 300ms
                Type "third"
                """;

        assertThat(problemsIn(script)).containsExactly("line 3: the previous command may still be running, so this"
                + " typing would mix into its output; add Sleep 300ms (or a Wait) after its Enter");
    }

    @Test
    void reportsNarrationThatWouldReadACommandAloud() throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/example-lesson/media"));
        Files.writeString(media.resolve("example-lesson.tape"), "# -- Scene 1: a --\nSleep 1s\n# -- Scene 2: b --\n");
        Files.writeString(media.resolve("example-lesson.narration.md"),
                "1. Now we run the weak tests.\n2. Run `./gradlew pitest` now.\n");

        assertThat(new VideoScriptChecker().check(root)).extracting(Problem::message)
                .containsExactly("item 2 contains a command, which the voice would read aloud; say what happens instead"
                        + " (for example, \"Now we run the strong tests\")");
    }

    @Test
    void reportsSpokenTextWithASemicolon() throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/example-lesson/media"));
        Files.writeString(media.resolve("example-lesson.tape"), "# -- Scene 1: a --\nSleep 1s\n");
        Files.writeString(media.resolve("example-lesson.narration.md"), "1. It passes; nobody notices.\n");
        Files.writeString(media.resolve("example-lesson.slides.md"), "Sources: a; b\n\n## intro: One\n- A; B\n"
                + "Say: Here is one.\n  It passes; nobody notices.\n\n## closing: More\n- {lessons-repo}\n"
                + "Say: Goodbye.\n");

        assertThat(new VideoScriptChecker().check(root)).extracting(Problem::message).containsExactly(
                "item 1 has a semicolon, which the voice runs on through; use a full stop",
                "line 6: what the slide says has a semicolon, which the voice runs on through; use a full stop");
    }

    @Test
    void reportsEscapedQuotesWhichVhsCannotRead() throws IOException {
        assertThat(problemsIn("Type \"export X=\\\"a b\\\"\"\nType `export X=\"a b\"`\n"))
                .containsExactly("line 1: VHS strings have no escape characters, so \\\" ends the string early;"
                        + " put the text in backticks instead");
    }

    @Test
    void findsNoScriptsWhenThereIsNoLessonsFolder() {
        assertThat(new VideoScriptChecker().check(root)).isEmpty();
    }

    @Test
    void mainPassesOnGoodScriptsAndFailsOnBadOnes() throws IOException {
        problemsIn("Type \"hello\"\n");
        VideoChecks.main(root.toString());
        problemsIn("Wait\n");

        assertAll(
                () -> assertThatThrownBy(() -> VideoChecks.main(root.toString()))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("2 problem(s)"),
                () -> assertThatThrownBy(VideoChecks::main).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(new Problem(root, "x").toString()).endsWith(": x"));
    }

    @Test
    void reportsAScriptWhoseScenesAndNarrationItemsDiffer() throws IOException {
        final Path tape = Files.writeString(root.resolve("x.tape"),
                "# ---- Scene 1: one ----\nHide\n# ---- Scene 1: one ----\nShow\n# ---- Scene 2: two ----\n");
        final Path narration = Files.writeString(root.resolve("x.narration.md"), "1. One.\n2. Two.\n");
        final Path matching = Files.writeString(root.resolve("y.narration.md"), "1. One.\n2. Two.\n3. Three.\n");

        assertAll(
                () -> assertThat(Narration.checkSceneCount(tape, narration)).get()
                        .extracting(Object::toString).asString().contains("3 scenes but 2 narration items"),
                () -> assertThat(Narration.checkSceneCount(tape, matching)).isEmpty());
    }
}
