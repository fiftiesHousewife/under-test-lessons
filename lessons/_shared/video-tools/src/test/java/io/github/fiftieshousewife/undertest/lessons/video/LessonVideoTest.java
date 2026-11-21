package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the voice and assembly steps with stand-ins for the voice, ffmpeg and ffprobe. */
class LessonVideoTest {

    @TempDir
    Path root;

    private Path media;

    @BeforeEach
    void lesson() throws IOException {
        media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.writeString(media.resolve("demo.tape"), "# ---- Scene 1: one ----\nSleep 3s\n# ---- Scene 2: two ----\n"
                + "Sleep 3s\n");
        Files.writeString(media.resolve("demo.narration.md"), "1. First scene.\n2. Second scene.\n");
        new WavFile(100, 1, new byte[200]).write(root.resolve("clip.wav"));
        System.setProperty("video.ffprobe", standIn("ffprobe", "echo 12.0").toString());
        System.setProperty("video.ffmpeg", standIn("ffmpeg", "for last; do :; done; touch \"$last\"").toString());
    }

    @AfterEach
    void restore() {
        System.clearProperty("video.ffprobe");
        System.clearProperty("video.ffmpeg");
    }

    private Path standIn(final String name, final String body) throws IOException {
        final Path script = root.resolve(name);
        Files.writeString(script, "#!/bin/sh\n" + body + "\n");
        assertThat(script.toFile().setExecutable(true)).isTrue();
        return script;
    }

    @Test
    void readsNarrationAloudAndBuildsATrackStretchedToTheVideo() throws IOException {
        Files.writeString(media.resolve("demo.silent.mp4"), "video");
        final Path voice = standIn("voice", "cp " + root.resolve("clip.wav") + " \"$2\"");

        LessonVideo.main("voice", root.toString(), "demo", voice.toString());

        assertAll(
                () -> assertThat(WavFile.read(media.resolve("demo.narration.wav")).seconds()).isEqualTo(12),
                () -> assertThat(Files.readString(media.resolve("demo.voice/02-1.txt"))).isEqualTo("Second scene."));
    }

    @Test
    void refusesNarrationThatDoesNotFit() throws IOException {
        new WavFile(100, 1, new byte[2000]).write(root.resolve("long.wav"));
        final Path voice = standIn("voice", "cp " + root.resolve("long.wav") + " \"$2\"");

        assertThatIllegalStateException()
                .isThrownBy(() -> LessonVideo.voice(new Media(root, "demo"), List.of(voice.toString())))
                .withMessageContaining("scene 1 (one): narration needs 11.0 s");
    }

    @Test
    void readsMeasuredRunTimesFromTheLastRecording() throws IOException {
        Files.writeString(media.resolve("demo.timings"), "2.5\n\n4.0\n");

        assertAll(
                () -> assertThat(new Media(root, "demo").measuredRuns()).containsExactly(2.5, 4.0),
                () -> assertThat(new Media(root, "other").measuredRuns()).isEmpty());
    }

    @Test
    void assemblesTheFinishedVideoAndNarrationAudio() {
        LessonVideo.main("assemble", root.toString(), "demo");

        assertAll(
                () -> assertThat(media.resolve("demo.mp4")).exists(),
                () -> assertThat(media.resolve("demo.narration.m4a")).exists());
    }

    @Test
    void explainsHowToUseIt() throws IOException {
        final Path failing = standIn("failing", "echo broken; exit 3");

        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> LessonVideo.main("dance", "x", "y")),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> LessonVideo.main("voice", "x", "y")),
                () -> assertThatIllegalStateException()
                        .isThrownBy(() -> ExternalCommand.run(List.of(failing.toString()), root))
                        .withMessageContaining("exit code 3").withMessageContaining("broken"),
                () -> assertThat(ExternalCommand.videoSeconds(root.resolve("missing.mp4"))).isEmpty());
    }
}
