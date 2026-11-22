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
        System.setProperty("video.browser", standIn("browser",
                "for a; do case \"$a\" in --screenshot=*) touch \"${a#--screenshot=}\";; esac; done").toString());
        final Path shared = Files.createDirectories(root.resolve("lessons/_shared/slides/pictures"));
        Files.writeString(shared.resolveSibling("slides.css"), "body { }");
    }

    @AfterEach
    void restore() {
        System.clearProperty("video.ffprobe");
        System.clearProperty("video.ffmpeg");
        System.clearProperty("video.browser");
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
    void assemblesTheFinishedVideoAndNarrationAudio() throws IOException {
        Files.writeString(media.resolve("demo.silent.mp4"), "video");
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\nSay: Hello there.\n"
                + "## closing: Bye\n- https://example.com\nSay: Goodbye.\n");
        final Path voice = standIn("voice", "cp " + root.resolve("clip.wav") + " \"$2\"");
        LessonVideo.main("voice", root.toString(), "demo", voice.toString());

        LessonVideo.main("assemble", root.toString(), "demo");

        assertAll(
                () -> assertThat(media.resolve("demo.mp4")).exists(),
                () -> assertThat(media.resolve("demo.narration.m4a")).exists(),
                () -> assertThat(media.resolve("demo.captioned.mp4.captions/001.png")).as("captions drawn").exists(),
                () -> assertThat(media.resolve("demo.voice/intro-1-0.png")).as("the title alone").exists(),
                () -> assertThat(media.resolve("demo.voice/intro-1-1.png")).as("with its point").exists(),
                () -> assertThat(Files.readString(media.resolve("demo.voice/intro-1-0.html"))).contains("later"),
                () -> assertThat(Files.readString(media.resolve("demo.vtt"))).contains("Hello there.")
                        .contains("First scene.").contains("Goodbye."));
    }

    @Test
    void zoomsTheRecordingWhereTheScriptAsksAndLeavesItAloneWhenTheRowsDontPairUp() throws IOException {
        final Path log = root.resolve("ffmpeg.log");
        System.setProperty("video.ffmpeg", standIn("ffmpeg", "echo \"$@\" >> " + log + "; for last; do :; done;"
                + " touch \"$last\"").toString());
        Files.writeString(media.resolve("demo.tape"), "Set Width 1920\nSet Height 1080\nSet Padding 24\n"
                + "# ---- Scene 1: one ----\nHide\nType@1ms `show Code.java 1 30 --zoom 5-7`\nEnter\nShow\nSleep 8s\n");
        Files.writeString(media.resolve("demo.silent.mp4"), "video");
        Files.writeString(media.resolve("demo.zooms"), "10 12 40\n");
        final Media demo = new Media(root, "demo");

        final Path zoomed = Zoom.zoomed(demo);
        Files.writeString(media.resolve("demo.zooms"), "10 12 40\n10 12 40\n");
        final Path unpaired = Zoom.zoomed(demo);

        assertAll(
                () -> assertThat(zoomed).isEqualTo(media.resolve("demo.zoomed.mp4")),
                () -> assertThat(Files.readString(log)).contains("-vf scale=w=").contains("demo.zoomed.mp4"),
                () -> assertThat(unpaired).isEqualTo(media.resolve("demo.silent.mp4")),
                () -> assertThat(Files.readAllLines(log)).hasSize(1));
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
