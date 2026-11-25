package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* A lesson's live-run scenes are cut out of its silent recording, one muted clip each, with the real ffmpeg. */
class LiveRunClipsTest {

    @TempDir
    Path root;

    private Media lesson() throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.write(media.resolve("demo.tape"), List.of(
                "# ---- Scene 1: the code ----", "Hide", "Type@1ms \"scene\"", "Enter", "Show", "Sleep 2s",
                "# ---- Scene 2: the run ----", "Hide", "Type@1ms \"scene\"", "Enter",
                "Type@1ms `run_live \"./gradlew test\"`", "Enter", "Show", "Sleep 2s"));
        return new Media(root, "demo");
    }

    @Test
    void cutsOneClipForTheLiveRunAndNoneForTheCode() throws IOException {
        Assumptions.assumeTrue(RealFfmpeg.installed(), "needs ffmpeg and ffprobe");
        final Media demo = lesson();
        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                "testsrc=size=1920x1080:rate=10", "-t", "4", "-pix_fmt", "yuv420p",
                demo.file(".silent.mp4").toString()), root);

        LiveRunClips.cut(demo);

        final Path clips = demo.file(LiveRunClips.CLIPS);
        assertAll(
                () -> assertThat(clips.resolve("scene-2.mp4")).isRegularFile(),
                () -> assertThat(clips.resolve("scene-1.mp4")).doesNotExist(),
                () -> assertThat(RealFfmpeg.probe(root, clips.resolve("scene-2.mp4"), "stream=width"))
                        .contains("1280"));
    }

    @Test
    void saysToRecordFirstWhenThereIsNoRecording() throws IOException {
        final Media demo = lesson();

        assertThatIllegalStateException().isThrownBy(() -> LiveRunClips.cut(demo))
                .withMessageContaining("record the lesson first");
    }
}
