package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TapeTimelineTest {

    @TempDir
    Path root;

    private List<Scene> scenesOf(final String script) throws IOException {
        final Path tape = root.resolve("lesson.tape");
        Files.writeString(tape, script);
        return TapeTimeline.scenes(root, tape);
    }

    @Test
    void countsOnlyTimeOnCamera() throws IOException {
        Files.writeString(root.resolve("setup.tape"), "Hide\nSleep 30s\nShow\n");
        final List<Scene> scenes = scenesOf("""
                Set TypingSpeed 100ms
                Source setup.tape
                # ---- Scene 1: why ----
                Sleep 2s
                Type "abcd"
                Hide
                Sleep 10s
                Wait+Screen /done/
                Show
                # ---- Scene 2: the build ----
                Type@10ms `ab`
                Sleep 500ms
                """);

        assertAll(
                () -> assertThat(scenes).extracting(Scene::title).containsExactly("why", "the build"),
                () -> assertThat(scenes.getFirst().start()).isZero(),
                () -> assertThat(scenes.getFirst().end()).isCloseTo(2.4, within(1e-9)),
                () -> assertThat(scenes.get(1).start()).isCloseTo(2.4, within(1e-9)),
                () -> assertThat(scenes.get(1).end()).isCloseTo(2.92, within(1e-9)));
    }

    @Test
    void rejectsAWaitOnCameraWithoutAMeasuredRunTime() {
        assertThatThrownBy(() -> scenesOf("# ---- Scene 1: x ----\nWait+Screen /done/\n"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("lesson.tape line 2")
                .hasMessageContaining("measured run time");
    }

    @Test
    void timesEachWaitOnCameraByItsMeasuredRun() throws IOException {
        final Path tape = root.resolve("live.tape");
        Files.writeString(tape, "# ---- Scene 1: live ----\nWait+Screen /a/\nSleep 1s\nWait+Screen /b/\n");

        assertThat(TapeTimeline.scenes(root, tape, List.of(2.5, 4.0)).getFirst().end()).isEqualTo(7.5);
    }

    @Test
    void convertsUnits() {
        assertAll(
                () -> assertThat(TapeTimeline.seconds("1.5", "s")).isEqualTo(1.5),
                () -> assertThat(TapeTimeline.duration("250ms")).isEqualTo(0.25),
                () -> assertThat(TapeTimeline.duration("2s")).isEqualTo(2));
    }

    @Test
    void ignoresTypeLinesWithoutQuotedText() throws IOException {
        assertThat(scenesOf("# ---- Scene 1: x ----\nType\nType unquoted\nSleep 1s\n").getFirst().end()).isEqualTo(1);
    }
}
