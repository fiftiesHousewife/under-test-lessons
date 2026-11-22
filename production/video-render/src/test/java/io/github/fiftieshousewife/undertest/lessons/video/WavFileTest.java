package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
}
