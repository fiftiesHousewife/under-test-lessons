package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* Captions burned into the picture: drawn in the look's colours, shown only while each one is spoken. */
class BurnedCaptionsTest {

    @TempDir
    Path root;

    @AfterEach
    void restore() {
        System.clearProperty("video.slideCss");
    }

    @Test
    void takesItsColoursFromTheLook() {
        System.setProperty("video.slideCss", "/repo/lessons/_shared/slides/themes/newspaper.css");
        final BurnedCaptions.Look newspaper = BurnedCaptions.look();
        System.setProperty("video.slideCss", "");
        final BurnedCaptions.Look colour = BurnedCaptions.look();

        assertAll(
                () -> assertThat(newspaper.text().getRGB() & 0xFFFFFF).isEqualTo(0x111111),
                () -> assertThat(newspaper.band().getRed()).isGreaterThan(200),
                () -> assertThat(colour.text().getRGB() & 0xFFFFFF).isEqualTo(0xFFFFFF),
                () -> assertThat(colour.band().getRed()).isLessThan(60));
    }

    @Test
    void drawsTheTextOnABandNearTheBottomAndLeavesTheRestSeeThrough() {
        final BurnedCaptions.Look look = BurnedCaptions.look();
        final BufferedImage picture = BurnedCaptions.picture("Run PIT against the weak tests.", look);

        assertAll(
                () -> assertThat(picture.getWidth()).isEqualTo(1920),
                () -> assertThat(picture.getHeight()).isEqualTo(1080),
                () -> assertThat(picture.getRGB(960, 100) >>> 24).as("top: see-through").isZero(),
                () -> assertThat(picture.getRGB(960, BurnedCaptions.bandTop(1)) >>> 24).as("band: solid").isPositive(),
                () -> assertThat(textPixels(picture, look)).as("the words are drawn").isGreaterThan(200));
    }

    private static long textPixels(final BufferedImage picture, final BurnedCaptions.Look look) {
        long count = 0;
        for (int y = 900; y < picture.getHeight(); y++) {
            for (int x = 0; x < picture.getWidth(); x += 2) {
                if ((picture.getRGB(x, y) & 0xFFFFFF) == (look.text().getRGB() & 0xFFFFFF)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Test
    void ffmpegShowsEachCaptionOnlyWhileItIsSpoken() {
        Assumptions.assumeTrue(RealFfmpeg.installed(), "needs ffmpeg and ffprobe");
        final Path video = root.resolve("joined.mp4");
        ExternalCommand.run(List.of("ffmpeg", "-y", "-loglevel", "error", "-f", "lavfi", "-i",
                "color=c=gray:size=1920x1080:rate=10", "-f", "lavfi", "-i", "anullsrc=r=48000:cl=stereo", "-t", "4",
                "-c:v", "libx264", "-pix_fmt", "yuv420p", "-c:a", "aac", "-shortest", video.toString()), root);
        final Path burned = root.resolve("burned.mp4");

        BurnedCaptions.burn(video, List.of(new Cue(1, 2, "First caption."), new Cue(2.5, 3.5, "Second.")), burned);

        assertAll(
                () -> assertThat(bottomDiffers(video, burned, 0.5)).as("before any caption").isFalse(),
                () -> assertThat(bottomDiffers(video, burned, 1.5)).as("during the first").isTrue(),
                () -> assertThat(bottomDiffers(video, burned, 2.2)).as("between them").isFalse(),
                () -> assertThat(bottomDiffers(video, burned, 3.0)).as("during the second").isTrue(),
                () -> assertThat(audioStreams(burned)).as("the sound is kept").isEqualTo(1));
    }


    private boolean bottomDiffers(final Path first, final Path second, final double seconds) {
        final String report = RealFfmpeg.psnr(root, new RealFfmpeg.Frames(first, second, seconds),
                "[0:v]crop=1920:300:0:780[a];[1:v]crop=1920:300:0:780[b];[a][b]psnr");
        final Matcher average = Pattern.compile("average:(\\S+)").matcher(report);
        return average.find() && !"inf".equals(average.group(1)) && Double.parseDouble(average.group(1)) < 40;
    }

    private long audioStreams(final Path video) {
        return ExternalCommand.run(List.of("ffprobe", "-v", "error", "-select_streams", "a", "-show_entries",
                "stream=index", "-of", "csv=p=0", video.toString()), root).lines().filter(l -> !l.isBlank()).count();
    }
}
