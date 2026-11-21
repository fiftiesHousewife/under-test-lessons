package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PronunciationTest {

    @TempDir
    Path folder;

    @Test
    void replacesWholeWordsForSpeechOnly() throws IOException {
        final Path file = Files.writeString(folder.resolve("pronunciations.txt"),
                "# How the voice says words\nPIT = pit\nJUnit = jay unit\nbroken line\n");
        final Pronunciation pronunciation = Pronunciation.read(file);

        assertAll(
                () -> assertThat(pronunciation.forSpeech("Run PIT, then JUnit. SPIT stays."))
                        .isEqualTo("Run pit, then jay unit. SPIT stays."),
                () -> assertThat(Pronunciation.read(folder.resolve("missing.txt")).forSpeech("PIT")).isEqualTo("PIT"));
    }
}
