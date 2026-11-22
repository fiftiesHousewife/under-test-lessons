package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Slides build up one point at a time through the narration, the picture easing in with the first point. */
class SlideBuildUpTest {

    private static final String PAGE = "<body class=\"frame-soft\"><main class=\"slide\"><ul>"
            + "<li class=\"point\">One</li><li class=\"point\">Two</li><li class=\"link\">https://x.org</li></ul>"
            + "<figure class=\"picture\"></figure></main></body>";

    @TempDir
    Path root;

    @Test
    void eachStageShowsThePointsSoFarAndKeepsTheRestInTheirPlaces() {
        final String first = SlideHtml.stage(PAGE, 1);
        final String none = SlideHtml.stage(PAGE, 0);

        assertAll(
                () -> assertThat(first).contains("<li class=\"point\">One</li>")
                        .contains("<li class=\"point later\">Two</li>").contains("<li class=\"link later\">")
                        .doesNotContain("before-picture"),
                () -> assertThat(none).contains("<li class=\"point later\">One</li>")
                        .contains("<body class=\"frame-soft before-picture\">"),
                () -> assertThat(SlideHtml.stage(PAGE, 3)).doesNotContain("later").doesNotContain("before-picture"));
    }

    @Test
    void pointsAppearEvenlyThroughTheFirstFourFifthsOfTheNarration() {
        assertAll(
                () -> assertThat(Splicer.appearances(2, 10)).containsExactly(4.0, 8.0),
                () -> assertThat(Splicer.appearances(1, 5)).singleElement().satisfies(
                        time -> assertThat(time).isCloseTo(4.0, within(1e-9))),
                () -> assertThat(Splicer.appearances(0, 5)).isEmpty());
    }

    @Test
    void ffmpegShowsEachStageInTurnAndKeepsTheSlidesLength() {
        Assumptions.assumeTrue(RealFfmpeg.installed(), "needs ffmpeg and ffprobe");
        final List<Path> stages = List.of(colour("red"), colour("lime"), colour("blue"));
        final Path voice = root.resolve("voice.wav");
        new WavFile(48_000, 1, new byte[48_000 * 2 * 3]).write(voice);
        final Path video = root.resolve("slide.mp4");

        ExternalCommand.run(Splicer.command(new Splicer.Cut(stages, voice, 3), video), root);

        assertAll(
                () -> assertThat(Double.parseDouble(RealFfmpeg.probe(root, video, "format=duration")))
                        .isBetween(4.45, 4.55),
                () -> assertThat(rgb(video, 0.8)).as("before the first point").isEqualTo("red"),
                () -> assertThat(rgb(video, 1.8)).as("after the first point").isEqualTo("green"),
                () -> assertThat(rgb(video, 3.6)).as("after the last point").isEqualTo("blue"));
    }

    private Path colour(final String name) {
        final Path picture = root.resolve(name + ".png");
        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                "color=c=" + name + ":size=1920x1080", "-frames:v", "1", picture.toString()), root);
        return picture;
    }

    /** Which of red, green or blue the frame at that moment mostly is. */
    @SuppressFBWarnings(value = "COMMAND_INJECTION", justification = "Runs ffmpeg on a video this test made itself")
    private String rgb(final Path video, final double seconds) {
        try {
            final Process ffmpeg = new ProcessBuilder("ffmpeg", "-loglevel", "error", "-ss",
                    String.format(Locale.ROOT, "%.2f", seconds), "-i", video.toString(), "-frames:v", "1", "-vf",
                    "scale=1:1", "-f", "rawvideo", "-pix_fmt", "rgb24", "-").start();
            final byte[] pixel = ffmpeg.getInputStream().readAllBytes();
            ffmpeg.waitFor();
            final int red = pixel[0] & 0xFF;
            final int green = pixel[1] & 0xFF;
            final int blue = pixel[2] & 0xFF;
            return red > green && red > blue ? "red" : green > blue ? "green" : "blue";
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
