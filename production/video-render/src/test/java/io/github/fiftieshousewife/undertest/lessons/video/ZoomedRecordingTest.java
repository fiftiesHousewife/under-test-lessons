package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* The ffmpeg filter that draws the zoom on key code into each frame. */
class ZoomedRecordingTest {

    private static final Zoom.Geometry FRAME = new Zoom.Geometry(new Zoom.Size(1920, 1080), 24, 30);

    @TempDir
    Path root;

    @Test
    void writesOneFilterThatScalesAndCropsEveryFrameToTheSameSize() {
        final String filter = ZoomedRecording.filter(FRAME,
                List.of(new ZoomTiming.Span(10, 20), new ZoomTiming.Span(30, 40)),
                List.of(Zoom.window(FRAME, new Zoom.Rows(20, 22, 40)), Zoom.window(FRAME, new Zoom.Rows(5, 6, 40))));

        assertAll(
                () -> assertThat(filter).startsWith("scale=w=").contains(":eval=frame,crop=w=1920:h=1080:x="),
                () -> assertThat(filter).contains("between(t,").doesNotContain("NaN").doesNotContain("Infinity"),
                () -> assertThat(ZoomedRecording.filter(FRAME, List.of(), List.of())).isEmpty());
    }

    @Test
    void ffmpegAcceptsTheFilterAndZoomsOnlyWhileTheSpanHolds() {
        Assumptions.assumeTrue(RealFfmpeg.installed(), "needs ffmpeg and ffprobe");
        final Path source = root.resolve("source.mp4");
        final Path zoomed = root.resolve("zoomed.mp4");
        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                "testsrc=size=1920x1080:rate=10", "-t", "6", "-pix_fmt", "yuv420p", source.toString()), root);
        final String filter = ZoomedRecording.filter(FRAME, List.of(new ZoomTiming.Span(0.5, 6)),
                List.of(Zoom.window(FRAME, new Zoom.Rows(20, 22, 40))));

        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-i", source.toString(), "-vf", filter,
                "-c:v", "libx264", "-pix_fmt", "yuv420p", zoomed.toString()), root);

        assertAll(
                () -> assertThat(RealFfmpeg.probe(root, zoomed, "stream=width,height")).isEqualTo("1920,1080"),
                () -> assertThat(RealFfmpeg.probe(root, zoomed, "stream=nb_frames"))
                        .isEqualTo(RealFfmpeg.probe(root, source, "stream=nb_frames")),
                () -> assertThat(frameDiffers(source, zoomed, 0.2)).as("before the zoom").isFalse(),
                () -> assertThat(frameDiffers(source, zoomed, 3.5)).as("while it holds").isTrue());
    }

    /* True if the two videos' frames at that moment differ by more than encoding noise (PSNR below 40 dB). */
    private boolean frameDiffers(final Path first, final Path second, final double seconds) {
        final String report = RealFfmpeg.psnr(root, new RealFfmpeg.Frames(first, second, seconds), "psnr");
        final java.util.regex.Matcher average = java.util.regex.Pattern.compile("average:(\\S+)").matcher(report);
        return !average.find() || !"inf".equals(average.group(1)) && Double.parseDouble(average.group(1)) < 40;
    }
}
