package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WavFileTest {

    @TempDir
    Path folder;

    @Test
    void writesReadsAndMeasuresSound() {
        final WavFile half = new WavFile(8000, 1, new byte[8000]);
        final Path file = folder.resolve("x.wav");
        half.write(file);
        final WavFile back = WavFile.read(file);

        assertAll(
                () -> assertThat(back.seconds()).isEqualTo(0.5),
                () -> assertThat(back.sampleRate()).isEqualTo(8000),
                () -> assertThat(back.channels()).isEqualTo(1),
                () -> assertThat(back.silence(2).seconds()).isEqualTo(2),
                () -> assertThat(back.frames(-1)).isZero());
    }

    @Test
    void rejectsFilesThatAreNotUsableWav() throws IOException {
        final Path text = Files.writeString(folder.resolve("t.wav"), "not a wav file at all");
        final Path noData = folder.resolve("n.wav");
        final byte[] header = new byte[44];
        new WavFile(8000, 1, new byte[0]).write(noData);
        Files.write(noData, java.util.Arrays.copyOf(Files.readAllBytes(noData), 36));
        final Path floatWav = folder.resolve("f.wav");
        new WavFile(8000, 1, header).write(floatWav);
        final byte[] bytes = Files.readAllBytes(floatWav);
        bytes[20] = 3;
        Files.write(floatWav, bytes);

        assertAll(
                () -> assertThatIllegalArgumentException().isThrownBy(() -> WavFile.read(text))
                        .withMessageContaining("is not a WAV file"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> WavFile.read(noData))
                        .withMessageContaining("no sound data"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> WavFile.read(floatWav))
                        .withMessageContaining("16-bit PCM"));
    }

    @Test
    void cutsLongGapsBetweenSoundsAndLeavesTheEdgesAndShortGaps() {
        final WavFile clip = sounds(0.3, -0.5, 1.0, -0.5, 0.2, -0.5, 0.3);

        final WavFile shorter = clip.withShorterGaps(0.4);

        assertAll(
                () -> assertThat(clip.seconds()).isCloseTo(3.3, within(1e-9)),
                () -> assertThat(shorter.seconds()).as("the 1 s gap cut to 0.4 s").isCloseTo(2.7, within(1e-9)),
                () -> assertThat(sounds(-0.5, 1.0).withShorterGaps(0.4).seconds()).as("quiet at the end kept")
                        .isCloseTo(1.5, within(1e-9)));
    }

    /* At 100 samples a second: a positive length is quiet for that long, a negative one sound. */
    static WavFile sounds(final double... lengths) {
        final java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        for (final double length : lengths) {
            final byte loud = length < 0 ? (byte) 0x10 : 0;
            for (int i = 0; i < Math.round(Math.abs(length) * 100); i++) {
                out.write(0);
                out.write(loud);
            }
        }
        return new WavFile(100, 1, out.toByteArray());
    }
}
