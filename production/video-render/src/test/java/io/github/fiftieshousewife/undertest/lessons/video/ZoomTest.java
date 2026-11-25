package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* The zoom on key code: where the window goes, how it eases, when it runs, and the ffmpeg filter that does it. */
class ZoomTest {

    private static final Zoom.Geometry FRAME = new Zoom.Geometry(new Zoom.Size(1920, 1080), 24, 30);

    @TempDir
    Path root;

    @Test
    void readsTheFrameFromTheScriptsSettings() throws IOException {
        final Path tape = root.resolve("demo.tape");
        Files.writeString(tape, "Set Width 1280\nSet Height 720\nSet Padding 10\nSet WindowBar Colorful\n");
        final Path plain = root.resolve("plain.tape");
        Files.writeString(plain, "Set FontSize 20\n");

        assertAll(
                () -> assertThat(Zoom.geometry(tape)).isEqualTo(new Zoom.Geometry(new Zoom.Size(1280, 720), 10, 30)),
                () -> assertThat(Zoom.geometry(plain).bar()).isZero(),
                () -> assertThat(Zoom.geometry(plain).frame()).isEqualTo(new Zoom.Size(1200, 600)));
    }

    @Test
    void framesTheMarkedRowsWithRoomAroundThemAnchoredLeft() {
        // 40 rows in 1080 - 30 - 2 * 24 = 1002 pixels: 25.05 pixels a row, the first row's top at 54.
        final Zoom.Window window = Zoom.window(FRAME, new Zoom.Rows(20, 22, 40));

        assertAll(
                () -> assertThat(window.left()).isZero(),
                () -> assertThat(window.width() / window.height()).isCloseTo(1920.0 / 1080, within(1e-9)),
                () -> assertThat(window.width()).isCloseTo(1920 / Zoom.MAX_ZOOM, within(1e-6)),
                () -> assertThat(window.height()).isCloseTo(1080 / Zoom.MAX_ZOOM, within(1e-6)),
                () -> assertThat(window.top()).isLessThan(54 + 19 * 25.05)
                        .isGreaterThan(54 + 22 * 25.05 - window.height()));
    }

    @Test
    void neverZoomsFurtherThanTheLimitNorOutsideTheFrame() {
        final Zoom.Window top = Zoom.window(FRAME, new Zoom.Rows(1, 1, 40));
        final Zoom.Window bottom = Zoom.window(FRAME, new Zoom.Rows(40, 40, 40));
        final Zoom.Window tall = Zoom.window(FRAME, new Zoom.Rows(1, 38, 40));

        assertAll(
                () -> assertThat(top.top()).isZero(),
                () -> assertThat(bottom.top() + bottom.height()).isCloseTo(1080, within(1e-6)),
                () -> assertThat(tall.width()).isCloseTo(1920, within(1e-6)),
                () -> assertThat(tall.top()).isZero());
    }

    @Test
    void easesInAfterTheCodeAppearsHoldsAndEasesOutBeforeTheSceneEnds() {
        final ZoomTiming.Span span = new ZoomTiming.Span(10, 20);

        assertAll(
                () -> assertThat(span.amount(10.5)).isZero(),
                () -> assertThat(span.amount(10 + ZoomTiming.DELAY + ZoomTiming.EASE / 2)).isCloseTo(0.5, within(1e-9)),
                () -> assertThat(span.amount(15)).isEqualTo(1),
                () -> assertThat(span.amount(20 - ZoomTiming.DELAY - ZoomTiming.EASE / 2)).isCloseTo(0.5, within(1e-9)),
                () -> assertThat(span.amount(19.5)).isZero(),
                () -> assertThat(new ZoomTiming.Span(10, 13).amount(11.5)).as("too short to zoom").isZero());
    }

    @Test
    void findsEachZoomFromWhenItsCodeAppearsToTheEndOfItsScene() throws IOException {
        final Path tape = root.resolve("demo.tape");
        Files.writeString(tape, """
                # ---- Scene 1: code ----
                Sleep 2s
                Hide
                Type@1ms `show Code.java 1 30 --zoom 5-7`
                Enter
                Show
                Sleep 8s
                # ---- Scene 2: plain ----
                Sleep 3s
                """);

        assertThat(TapeTimeline.zooms(root, tape, List.of())).containsExactly(new ZoomTiming.Span(2, 10));
    }

    @Test
    void endsTheZoomWhenTheSceneDrawsSomethingElseSoACalloutBelowIsSeen() throws IOException {
        final Path tape = root.resolve("demo.tape");
        Files.writeString(tape, """
                # ---- Scene 1: code, then a callout ----
                Hide
                Type@1ms `show Code.java 1 30 --zoom 5-7`
                Enter
                Show
                Sleep 6s
                Hide
                Type@1ms `callout "Look"`
                Enter
                Show
                Sleep 5s
                """);

        assertThat(TapeTimeline.zooms(root, tape, List.of())).containsExactly(new ZoomTiming.Span(0, 6));
    }

    @Test
    void writesOneFilterThatScalesAndCropsEveryFrameToTheSameSize() {
        final String filter = Zoom.filter(FRAME, List.of(new ZoomTiming.Span(10, 20), new ZoomTiming.Span(30, 40)),
                List.of(Zoom.window(FRAME, new Zoom.Rows(20, 22, 40)), Zoom.window(FRAME, new Zoom.Rows(5, 6, 40))));

        assertAll(
                () -> assertThat(filter).startsWith("scale=w=").contains(":eval=frame,crop=w=1920:h=1080:x="),
                () -> assertThat(filter).contains("between(t,").doesNotContain("NaN").doesNotContain("Infinity"),
                () -> assertThat(Zoom.filter(FRAME, List.of(), List.of())).isEmpty());
    }

    @Test
    void ffmpegAcceptsTheFilterAndZoomsOnlyWhileTheSpanHolds() {
        Assumptions.assumeTrue(RealFfmpeg.installed(), "needs ffmpeg and ffprobe");
        final Path source = root.resolve("source.mp4");
        final Path zoomed = root.resolve("zoomed.mp4");
        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                "testsrc=size=1920x1080:rate=10", "-t", "6", "-pix_fmt", "yuv420p", source.toString()), root);
        final String filter = Zoom.filter(FRAME, List.of(new ZoomTiming.Span(0.5, 6)),
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
        final String at = String.format(java.util.Locale.ROOT, "%.2f", seconds);
        final String report = ExternalCommand.run(List.of("ffmpeg", "-ss", at, "-i", first.toString(), "-ss", at,
                "-i", second.toString(), "-frames:v", "1", "-lavfi", "psnr", "-f", "null", "-"), root);
        final java.util.regex.Matcher average = java.util.regex.Pattern.compile("average:(\\S+)").matcher(report);
        return !average.find() || !"inf".equals(average.group(1)) && Double.parseDouble(average.group(1)) < 40;
    }

    @Test
    void readsTheRowsTheHelperReportedKeepingTheirOrder() throws IOException {
        final Path zooms = root.resolve("demo.zooms");
        Files.writeString(zooms, "20 22 40\nnot rows\n5 6 40\n");

        assertAll(
                () -> assertThat(Zoom.rows(zooms)).containsExactly(Optional.of(new Zoom.Rows(20, 22, 40)),
                        Optional.empty(), Optional.of(new Zoom.Rows(5, 6, 40))),
                () -> assertThat(Zoom.rows(root.resolve("missing.zooms"))).isEmpty());
    }
}
