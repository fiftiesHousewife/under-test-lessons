package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** What the short check recording must report before any lesson is recorded. */
class VideoCheckTest {

    @TempDir
    Path root;

    @AfterEach
    void restore() {
        List.of("video.vhs", "video.ffmpeg", "video.browser").forEach(System::clearProperty);
    }

    private Path standIn(final String name, final String body) throws IOException {
        final Path script = root.resolve(name);
        Files.writeString(script, "#!/bin/sh\n" + body + "\n");
        assertThat(script.toFile().setExecutable(true)).isTrue();
        return script;
    }

    @Test
    void runsEveryStepInTurnAndLeavesTheVideoToWatch() throws IOException {
        final Path shared = Files.createDirectories(root.resolve("lessons/_shared/slides/pictures"));
        Files.writeString(shared.resolveSibling("slides.css"), "body { }");
        Files.writeString(shared.resolve("checklist.svg"), "<svg></svg>");
        Files.createDirectories(root.resolve("production/video-render"));
        Files.copy(Path.of("video-check.tape"), root.resolve(VideoCheck.TAPE));
        System.setProperty("video.vhs", standIn("vhs", "mkdir -p build/video-check; echo '7 9 41' >"
                + " build/video-check/video-check.zooms; touch build/video-check/video-check.silent.mp4").toString());
        System.setProperty("video.ffmpeg", standIn("ffmpeg", "for last; do :; done; touch \"$last\"").toString());
        System.setProperty("video.browser", standIn("browser",
                "for a; do case \"$a\" in --screenshot=*) touch \"${a#--screenshot=}\";; esac; done").toString());

        VideoCheck.main(root.toString());

        assertAll(
                () -> assertThat(root.resolve("build/video-check/video-check.mp4")).exists(),
                () -> assertThat(root.resolve(
                        "lessons/mutation-testing/media/mutation-testing.voice/video-check-2.png"))
                        .as("the slide's last stage").exists());
    }

    @Test
    void passesThreeLinesOnScreenAndSaysWhatIsWrongOtherwise() {
        assertAll(
                () -> assertThat(VideoCheck.problem(List.of(Optional.of(new Zoom.Rows(8, 10, 40))))).isEmpty(),
                () -> assertThat(VideoCheck.problem(List.of())).hasValueSatisfying(
                        problem -> assertThat(problem).contains("got 0").contains("VIDEO_ZOOMS")),
                () -> assertThat(VideoCheck.problem(List.of(Optional.empty()))).hasValueSatisfying(
                        problem -> assertThat(problem).contains("didn't say where the cursor was")),
                () -> assertThat(VideoCheck.problem(List.of(Optional.of(new Zoom.Rows(8, 12, 40))))).isPresent(),
                () -> assertThat(VideoCheck.problem(List.of(Optional.of(new Zoom.Rows(39, 41, 40))))).isPresent());
    }

    @Test
    void theCheckRecordingsZoomHasTimeToRun() {
        final Path repo = java.nio.file.Path.of("../..").toAbsolutePath().normalize();

        assertAll(
                () -> assertThat(ZoomChecks.problems(repo, repo.resolve(VideoCheck.TAPE))).isEmpty(),
                () -> assertThat(TapeTimeline.zooms(repo, repo.resolve(VideoCheck.TAPE), List.of())).hasSize(1));
    }
}
