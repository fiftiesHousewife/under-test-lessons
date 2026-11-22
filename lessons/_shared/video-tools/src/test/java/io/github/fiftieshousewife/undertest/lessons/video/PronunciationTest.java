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

    @Test
    void matchesWordsThatStartOrEndWithASign() throws IOException {
        final Path file = Files.writeString(folder.resolve("pronunciations.txt"),
                "C++ = see plus plus\n.NET = dot net\n");
        assertThat(Pronunciation.read(file).forSpeech("Old C++ and .NET code."))
                .isEqualTo("Old see plus plus and dot net code.");
    }

    @Test
    void appliesEveryRuleInOnePassLongestFirst() throws IOException {
        final Path file = Files.writeString(folder.resolve("pronunciations.txt"),
                "record = /ɹɪkˈɔːd/\neach record = each /ɹˈɛkɔːd/\nAPI = A P I\nA P I = never\n");
        assertThat(Pronunciation.read(file).forSpeech("Record each record, then record the API."))
                .isEqualTo("Record each /ɹˈɛkɔːd/, then /ɹɪkˈɔːd/ the A P I.");
    }

    @Test
    void saysSumsOfMoneyAsAPersonDoes() throws IOException {
        final Path file = Files.writeString(folder.resolve("p.txt"), "PIT = pit\n");
        final Pronunciation pronunciation = Pronunciation.read(file);
        assertThat(pronunciation.forSpeech("£12.50, £1, £150, £1,200, £0.50 and £49.99 with PIT"))
                .isEqualTo("12 pounds 50, 1 pound, 150 pounds, 1200 pounds, 50 pence and 49 pounds 99 with pit");
    }
}
