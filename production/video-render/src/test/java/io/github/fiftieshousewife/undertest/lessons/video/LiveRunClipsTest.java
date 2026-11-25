package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* Each live-run scene gets a script of its own, the lesson's settings then that scene, and becomes a small clip. */
class LiveRunClipsTest {

    private static final List<String> SCRIPT = List.of(
            "Output lessons/demo/media/demo.gif", "Output lessons/demo/media/demo.silent.mp4", "Set Width 1920",
            "Source lessons/_shared/setup.tape",
            "# ---- Scene 1: the code ----", "Hide", "Show", "Sleep 2s",
            "# ---- Scene 2: the run ----", "Hide", "Type@1ms `run_live \"./gradlew test\"`", "Show", "Sleep 2s");

    @TempDir
    Path root;

    @Test
    void splitsTheScriptIntoItsSettingsAndEachScene() {
        final List<List<String>> scenes = LiveRunClips.scenes(SCRIPT);

        assertAll(
                () -> assertThat(LiveRunClips.settings(SCRIPT)).hasSize(4).last().isEqualTo(
                        "Source lessons/_shared/setup.tape"),
                () -> assertThat(scenes).hasSize(2),
                () -> assertThat(scenes.get(1)).first().isEqualTo("# ---- Scene 2: the run ----"));
    }

    @Test
    void aClipScriptHasOneOutputTheSettingsAndTheSceneAlone() {
        final List<String> clip = LiveRunClips.clipScript(LiveRunClips.settings(SCRIPT),
                LiveRunClips.scenes(SCRIPT).get(1), Path.of("lessons/demo/media/demo.deck-clips/scene-2.raw.mp4"));

        assertAll(
                () -> assertThat(clip).filteredOn(line -> line.startsWith("Output "))
                        .containsExactly("Output lessons/demo/media/demo.deck-clips/scene-2.raw.mp4"),
                () -> assertThat(clip).contains("Set Width 1920", "Source lessons/_shared/setup.tape")
                        .doesNotContain("# ---- Scene 1: the code ----"),
                () -> assertThat(clip).last().isEqualTo("Sleep 2s"));
    }

    @Test
    void shrinksARecordedSceneToASmallClipAndRemovesTheRecording() {
        Assumptions.assumeTrue(RealFfmpeg.installed(), "needs ffmpeg and ffprobe");
        final Path raw = root.resolve("scene-2.raw.mp4");
        final Path clip = root.resolve("scene-2.mp4");
        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                "testsrc=size=1920x1080:rate=10", "-t", "2", "-pix_fmt", "yuv420p", raw.toString()), root);

        LiveRunClips.shrink(raw, clip);

        assertAll(
                () -> assertThat(raw).doesNotExist(),
                () -> assertThat(RealFfmpeg.probe(root, clip, "stream=width")).contains("1280"));
    }
}
