package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Slides fade up from black and back, with a slow zoom between, without changing how long they show. */
class SlideMotionTest {

    @TempDir
    Path root;

    @Test
    void theFilterFadesBothEndsAndZoomsSlowlyAndAlwaysGivesTheSameSize() {
        final String filter = Splicer.motion(6);

        assertAll(
                () -> assertThat(filter).contains("fade=t=in:st=0:d=0.333").contains("fade=t=out:st=5.667:d=0.333"),
                () -> assertThat(filter).contains("eval=frame").contains("crop=w=1920:h=1080"),
                () -> assertThat(Splicer.motion(0.5)).as("too short to fade both ways").doesNotContain("fade"));
    }

    @Test
    void ffmpegFadesFromBlackHoldsThePictureAndFadesBackKeepingTheLength() {
        Assumptions.assumeTrue(RealFfmpeg.installed(), "needs ffmpeg and ffprobe");
        final Path picture = root.resolve("slide.png");
        final Path video = root.resolve("slide.mp4");
        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                "testsrc=size=1920x1080:rate=1", "-frames:v", "1", picture.toString()), root);

        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-loop", "1", "-framerate", "30",
                "-i", picture.toString(), "-vf", Splicer.motion(3), "-t", "3", "-c:v", "libx264", "-pix_fmt",
                "yuv420p", video.toString()), root);

        assertAll(
                () -> assertThat(RealFfmpeg.probe(root, video, "stream=width,height")).isEqualTo("1920,1080"),
                () -> assertThat(Double.parseDouble(RealFfmpeg.probe(root, video, "format=duration")))
                        .isBetween(2.95, 3.05),
                () -> assertThat(brightness(video, 0.0)).as("first frame, from black").isLessThan(20),
                () -> assertThat(brightness(video, 1.5)).as("the middle").isGreaterThan(60),
                () -> assertThat(brightness(video, 2.97)).as("last frame, to black").isLessThan(30));
    }



    /** The average brightness (0 to 255) of the frame at that moment. */
    private double brightness(final Path video, final double seconds) {
        final String at = String.format(Locale.ROOT, "%.2f", seconds);
        final String report = ExternalCommand.run(List.of("ffmpeg", "-ss", at,
                "-i", video.toString(), "-frames:v", "1", "-vf",
                "signalstats,metadata=print:key=lavfi.signalstats.YAVG",
                "-f", "null", "-"), root);
        final Matcher average = Pattern.compile("YAVG=([0-9.]+)").matcher(report);
        return average.find() ? Double.parseDouble(average.group(1)) : -1;
    }
}
