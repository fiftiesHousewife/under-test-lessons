package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** A video's length, estimated before recording, and the ten-minute limit. */
class VideoLengthTest {

    @TempDir
    Path root;

    private Path media;

    @BeforeEach
    void lesson() throws IOException {
        media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.createDirectories(root.resolve("lessons/_shared/slides"));
        // 13 words: 5 s spoken, plus the 1.5 s each slide stays up after its narration.
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\n"
                + "Say: one two three four five six seven eight nine ten eleven twelve thirteen\n");
    }

    @Test
    void addsTheSlidesTheRecordingAndAGuessForEachUntimedLiveRun() throws IOException {
        Files.writeString(media.resolve("demo.tape"), "# ---- Scene 1: run ----\nSleep 10s\nWait+Screen /x/\n");

        assertThat(VideoLength.estimate(new Media(root, "demo")))
                .isCloseTo(5 + 1.5 + 10 + VideoLength.UNTIMED_RUN_SECONDS, within(1e-9));
    }

    @Test
    void usesTheMeasuredRunTimesOnceARecordingHasThem() throws IOException {
        Files.writeString(media.resolve("demo.tape"), "# ---- Scene 1: run ----\nSleep 10s\nWait+Screen /x/\n");
        Files.writeString(media.resolve("demo.timings"), "4.5\n");

        assertThat(VideoLength.estimate(new Media(root, "demo"))).isCloseTo(5 + 1.5 + 10 + 4.5, within(1e-9));
    }

    @Test
    void refusesAVideoOverTenMinutesAndSaysByHowMuch() throws IOException {
        Files.writeString(media.resolve("demo.tape"), "# ---- Scene 1: long ----\nSleep 600s\n");
        final Path short1 = Files.createDirectories(root.resolve("lessons/brief/media"));
        Files.writeString(short1.resolve("brief.tape"), "# ---- Scene 1: short ----\nSleep 60s\n");

        assertAll(
                () -> assertThat(VideoLength.problems(new Media(root, "demo"))).singleElement()
                        .satisfies(problem -> assertThat(problem.message()).contains("10 min 7 s")
                                .contains("ten minutes")),
                () -> assertThat(VideoLength.problems(new Media(root, "brief"))).isEmpty());
    }
}
