package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/* Tests the deck's voice with stand-ins for the voice and ffmpeg: which slides it reads, and which clips it keeps. */
class DeckVoiceTest extends DeckVoiceLesson {

    @Test
    void speaksEachDeckSlideInOrderAsTheVoiceShouldSayIt() {
        assertThat(deckSays()).containsExactly("Hello there.", "Run pit now.", "Goodbye.");
    }

    @Test
    void namesEachClipByItsWrittenWordsAlone() {
        assertAll(
                () -> assertThat(clip("Hello there.")).isEqualTo(clip("Hello there.")).matches("[0-9a-f]{16}\\.m4a"),
                () -> assertThat(clip("Hello there.")).isNotEqualTo(clip("Hello.")),
                () -> assertThat(new Spoken("Run PIT now.", "Run pit now.").clipName()).isEqualTo(clip("Run PIT now.")),
                () -> assertThat(clip("Hello [pause 1s] there.")).isEqualTo(clip("Hello\n  there.")));
    }

    @Test
    void readsOnlySlidesWithoutAClipAndRemovesClipsTheVoiceMadeThatNoSlideUses() throws IOException {
        final Media demo = new Media(root, "demo");
        final Path folder = media.resolve("demo.deck-voice");
        final int first = DeckReader.speak(demo, List.of(voice.toString()));
        final long partsAfterFirst = spokenParts();
        Files.writeString(folder.resolve("notes.txt"), "not a clip");

        final int second = DeckReader.speak(demo, List.of(voice.toString()));
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\nSay: Hello there.\n");
        DeckReader.speak(demo, List.of(voice.toString()));

        assertAll(
                () -> assertThat(first).isEqualTo(3),
                () -> assertThat(second).isZero(),
                () -> assertThat(spokenParts()).isEqualTo(partsAfterFirst),
                () -> assertThat(folder.resolve(clip("Goodbye."))).doesNotExist(),
                () -> assertThat(folder.resolve(clip("Hello there."))).exists(),
                () -> assertThat(folder.resolve("notes.txt")).exists());
    }

    @Test
    void aPronunciationFixReadsTheClipAgainUnderTheSameName() throws IOException {
        final Media demo = new Media(root, "demo");
        DeckReader.speak(demo, List.of(voice.toString()));
        Files.writeString(root.resolve("lessons/_shared/pronunciations.txt"), "PIT = pitt\n");

        final int made = DeckReader.speak(demo, List.of(voice.toString()));

        assertAll(
                () -> assertThat(made).isEqualTo(1),
                () -> assertThat(media.resolve("demo.deck-voice").resolve(clip("Run PIT now."))).exists());
    }

    @Test
    void aNewVoiceEngineReadsEveryClipAgainAndRecordsItself() throws IOException {
        final Media demo = new Media(root, "demo");
        System.setProperty(Speech.ENGINE_PROPERTY, "kokoro/one");
        DeckReader.speak(demo, List.of(voice.toString()));
        final int unchanged = DeckReader.speak(demo, List.of(voice.toString()));
        System.setProperty(Speech.ENGINE_PROPERTY, "kokoro/two");

        final int made = DeckReader.speak(demo, List.of(voice.toString()));

        assertAll(
                () -> assertThat(unchanged).isZero(),
                () -> assertThat(made).isEqualTo(3),
                () -> assertThat(Files.readString(media.resolve("demo.deck-voice").resolve(ClipLedger.FILE)))
                        .contains(clip("Goodbye.") + " voice").contains(" kokoro/two").doesNotContain("kokoro/one"));
    }

    @Test
    void aClipAPersonRecordedIsNeverReadAgainNorRemoved() throws IOException {
        final Path folder = Files.createDirectories(media.resolve("demo.deck-voice"));
        Files.writeString(folder.resolve(clip("Hello there.")), "a person's recording");
        Files.writeString(folder.resolve("0000000000000000.m4a"), "another recording, of a slide since removed");

        final int made = DeckReader.speak(new Media(root, "demo"), List.of(voice.toString()));

        assertAll(
                () -> assertThat(made).isEqualTo(2),
                () -> assertThat(folder.resolve(clip("Hello there."))).hasContent("a person's recording"),
                () -> assertThat(folder.resolve("0000000000000000.m4a")).exists());
    }

    @Test
    void aClipUnderItsEarlierNameIsRenamedNotReadAgain() throws IOException {
        final Path folder = Files.createDirectories(media.resolve("demo.deck-voice"));
        final Spoken run = new Spoken("Run PIT now.", "Run pit now.");
        Files.writeString(folder.resolve(run.earlierClipName()), "read before");

        DeckReader.speak(new Media(root, "demo"), List.of(voice.toString()));

        assertAll(
                () -> assertThat(folder.resolve(run.clipName())).hasContent("read before"),
                () -> assertThat(folder.resolve(run.earlierClipName())).doesNotExist(),
                () -> assertThat(spokenParts()).isEqualTo(2));
    }

    @Test
    void clipsMadeBeforeLedgersAreTheVoicesSoAPronunciationFixStillReadsThemAgain() throws IOException {
        final Path folder = Files.createDirectories(media.resolve("demo.deck-voice"));
        Files.writeString(folder.resolve(clip("Goodbye.")), "read before ledgers");
        DeckReader.speak(new Media(root, "demo"), List.of(voice.toString()));
        final long partsBeforeTheFix = spokenParts();
        Files.writeString(root.resolve("lessons/_shared/pronunciations.txt"), "PIT = pit\nGoodbye = good bye\n");

        final int made = DeckReader.speak(new Media(root, "demo"), List.of(voice.toString()));

        assertAll(
                () -> assertThat(partsBeforeTheFix).as("read before the fix: the two slides with no clip").isEqualTo(2),
                () -> assertThat(made).as("read again after the fix: the clip made before ledgers").isEqualTo(1));
    }
}
