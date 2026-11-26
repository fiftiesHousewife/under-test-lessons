package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/*
 * A lesson's live-run scenes that changed are recorded in one session, from the lesson's settings and those scenes,
 * and each is cut out as a small clip; a clip whose checksum still matches is kept.
 */
class LiveRunClipsTest {

    private static final List<String> SCRIPT = List.of(
            "Output lessons/demo/media/demo.gif", "Output lessons/demo/media/demo.silent.mp4", "Set Width 1920",
            "Source lessons/_shared/setup.tape",
            "# ---- Scene 1: the code ----", "Hide", "Show", "Sleep 2s",
            "# ---- Scene 2: the run ----", "Hide", "Type@1ms `run_live \"./gradlew test\"`", "Show", "Sleep 2s");

    @TempDir
    Path root;

    private Media lesson() throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.write(media.resolve("demo.tape"), SCRIPT);
        Files.createDirectories(root.resolve("lessons/demo/example/src"));
        Files.writeString(root.resolve("lessons/demo/example/src/Demo.java"), "class Demo {}");
        return new Media(root, "demo");
    }

    @Test
    void aSessionScriptHasOneOutputTheSettingsAndTheScenesAlone() {
        final Path output = Path.of("lessons/demo/media/demo.deck-clips/session.raw.mp4");
        final List<String> session = LiveRunClips.sessionScript(LiveRunClips.settings(SCRIPT),
                List.of(LiveRunClips.scenes(SCRIPT).get(1)), output);

        assertAll(
                () -> assertThat(session).filteredOn(line -> line.startsWith("Output "))
                        .containsExactly("Output lessons/demo/media/demo.deck-clips/session.raw.mp4"),
                () -> assertThat(session).contains("Set Width 1920", "Source lessons/_shared/setup.tape")
                        .doesNotContain("# ---- Scene 1: the code ----"),
                () -> assertThat(session).last().isEqualTo("Sleep 2s"));
    }

    @Test
    void onlyALiveRunWithNoMatchingChecksumIsRecordedAgain() throws IOException {
        final Media demo = lesson();
        final Path clips = Files.createDirectories(demo.file(LiveRunClips.CLIPS));
        final List<List<String>> scenes = LiveRunClips.scenes(SCRIPT);
        final List<Integer> before = LiveRunClips.changed(demo, scenes, clips);
        Files.writeString(clips.resolve(LiveRunClips.name(1)), "a clip");
        SceneChecksum.write(clips, 1, SceneChecksum.of(demo, scenes.get(1)));
        final List<Integer> recorded = LiveRunClips.changed(demo, scenes, clips);
        Files.writeString(root.resolve("lessons/demo/example/src/Demo.java"), "class Demo { int changed; }");

        assertAll(
                () -> assertThat(before).as("a live run with no clip yet").containsExactly(1),
                () -> assertThat(recorded).as("its clip kept, the code scene never recorded").isEmpty(),
                () -> assertThat(LiveRunClips.changed(demo, scenes, clips)).as("once the lesson's code changes")
                        .containsExactly(1));
    }

    @Test
    void cutsOneSceneOutOfTheSessionAsASmallClip() {
        Assumptions.assumeTrue(RealFfmpeg.installed(), "needs ffmpeg and ffprobe");
        final Path raw = root.resolve("session.raw.mp4");
        final Path clip = root.resolve("scene-2.mp4");
        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                "testsrc=size=1920x1080:rate=10", "-t", "4", "-pix_fmt", "yuv420p", raw.toString()), root);

        LiveRunClips.cut(raw, new Scene("the run", 1, 3), clip);

        assertAll(
                () -> assertThat(RealFfmpeg.probe(root, clip, "stream=width")).contains("1280"),
                () -> assertThat(seconds(RealFfmpeg.probe(root, clip, "format=duration"))).as("seconds")
                        .isBetween(1.8, 2.2));
    }

    private static double seconds(final String probed) {
        return Double.parseDouble(probed.replaceAll("[^0-9.]", ""));
    }
}
