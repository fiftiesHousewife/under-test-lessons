package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CaptionsTest {

    @TempDir
    Path root;

    @Test
    void writesCaptionFilesForEveryLessonWithNarration() throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.createDirectories(root.resolve("lessons/_shared"));
        Files.writeString(media.resolve("demo.tape"), "# ---- Scene 1: only ----\nSleep 4s\n");
        Files.writeString(media.resolve("demo.narration.md"), "1. The only scene.\n");

        Captions.main(root.toString());

        assertAll(
                () -> assertThat(Files.readString(media.resolve("demo.vtt"))).contains("00:00:00.000 --> 00:00:04.000"),
                () -> assertThat(Files.readString(media.resolve("demo.srt"))).contains("The only scene."),
                () -> assertThatIllegalArgumentException().isThrownBy(Captions::main));
    }
}
