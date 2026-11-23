package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClipLedgerTest {

    @TempDir
    Path folder;

    private ClipLedger ledger(final String lines) throws IOException {
        Files.writeString(folder.resolve(ClipLedger.FILE), lines);
        return ClipLedger.read(folder);
    }

    @Test
    void aClipCountsAsCurrentOnlyWithTheSameWordsAndEngine() throws IOException {
        final ClipLedger ledger = ledger("a.m4a voice k1 kokoro/one\n");

        assertAll(
                () -> assertThat(ledger.readFrom("a.m4a", "k1", "kokoro/one")).isTrue(),
                () -> assertThat(ledger.readFrom("a.m4a", "k1", "kokoro/two")).as("another engine").isFalse(),
                () -> assertThat(ledger.readFrom("a.m4a", "k2", "kokoro/one")).as("other words").isFalse());
    }

    @Test
    void aLineFromBeforeEnginesCountsAsThisEnginesAndIsStampedWithIt() throws IOException {
        final ClipLedger ledger = ledger("a.m4a voice k1\n");
        final boolean current = ledger.readFrom("a.m4a", "k1", "kokoro/one");

        ledger.stampEngine("a.m4a", "kokoro/one");
        ledger.write();

        assertAll(
                () -> assertThat(current).isTrue(),
                () -> assertThat(Files.readString(folder.resolve(ClipLedger.FILE)))
                        .contains("a.m4a voice k1 kokoro/one"),
                () -> assertThat(ClipLedger.read(folder).readFrom("a.m4a", "k1", "kokoro/two")).isFalse());
    }

    @Test
    void saysWhoMadeEachClipInTheMixOfVoices() throws IOException {
        final ClipLedger ledger = ledger("a.m4a voice k1 kokoro/one\nb.m4a voice k2 kokoro/one\n"
                + "c.wav person Jane Smith\nd.m4a voice k4\n");

        final Map<String, Integer> mix = ledger.mix(List.of("a.m4a", "b.m4a", "c.wav", "d.m4a", "e.mp3"));

        assertAll(
                () -> assertThat(mix).containsEntry("the voice kokoro/one", 2),
                () -> assertThat(mix).containsEntry("Jane Smith", 1),
                () -> assertThat(mix).containsEntry("the voice (engine unrecorded)", 1),
                () -> assertThat(mix).containsEntry("a person", 1),
                () -> assertThat(ledger.byTheVoice("c.wav")).isFalse());
    }
}
