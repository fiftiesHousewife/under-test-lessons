package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/* Tests the deck's voice with stand-ins for the voice and ffmpeg: the deck that plays it, and the store of clips. */
class DeckVoiceSharedTest extends DeckVoiceLesson {

    @Test
    void theDeckPlaysTheClipsItHasAndSaysNothingWhereItHasNone() throws IOException {
        final Path folder = Files.createDirectories(media.resolve("demo.deck-voice"));
        Files.writeString(folder.resolve(clip("Hello there.")), "sound");

        final String deck = SlideDeck.html(new Media(root, "demo"));

        assertAll(
                () -> assertThat(deck).containsOnlyOnce("<audio class=\"voice\"")
                        .contains("src=\"demo.deck-voice/" + clip("Hello there.") + "\""),
                () -> assertThat(DeckVoice.sources(new Media(root, "demo")))
                        .containsExactly("demo.deck-voice/" + clip("Hello there."), "", ""),
                () -> assertThat(DeckVoice.withVoice("<section>x</section>", "")).isEqualTo("<section>x</section>"));
    }

    @Test
    void theCommandReadsTheDeckAloudAndRewritesTheDeck() {
        LessonDeckVoice.main(root.toString(), "demo", voice.toString());

        assertAll(
                () -> assertThat(Files.readString(media.resolve("demo.deck.html"))).contains("<audio class=\"voice\""),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> LessonDeckVoice.main(root.toString(), "demo")));
    }

    @Test
    void aVoiceThatReadsListsIsStartedOnceForTheWholeDeck() throws IOException {
        final Path listVoice = standIn("list-voice", "echo started >> " + root.resolve("starts.log")
                + "; [ \"$1\" = --list ] || exit 2; while IFS=\"$(printf '\\t')\" read -r text wav; do"
                + " cp " + root.resolve("clip.wav") + " \"$wav\"; done < \"$2\"");
        System.setProperty(Speech.READS_LISTS, "true");

        final int made = DeckReader.speak(new Media(root, "demo"), List.of(listVoice.toString()));

        assertAll(
                () -> assertThat(made).isEqualTo(3),
                () -> assertThat(Files.readAllLines(root.resolve("starts.log"))).hasSize(1),
                () -> assertThat(media.resolve("demo.deck-voice").resolve(clip("Goodbye."))).exists());
    }

    @Test
    void neverReadsASlideAPersonRecordedAsAWav() throws IOException {
        final Path folder = Files.createDirectories(media.resolve("demo.deck-voice"));
        final Path recorded = Files.writeString(folder.resolve(new Spoken("Goodbye.", "Goodbye.").clipStem() + ".wav"),
                "a person's recording");

        final int made = DeckReader.speak(new Media(root, "demo"), List.of(voice.toString()));

        assertAll(
                () -> assertThat(made).isEqualTo(2),
                () -> assertThat(Files.readString(recorded)).isEqualTo("a person's recording"),
                () -> assertThat(folder.resolve(clip("Goodbye."))).doesNotExist());
    }

    @Test
    void aDeckClipThatIsLostIsMadeAgainFromTheStoreWithoutReadingItAloud() throws IOException {
        final Media demo = new Media(root, "demo");
        DeckReader.speak(demo, List.of(voice.toString()));
        final long partsAfterFirst = spokenParts();
        Files.delete(media.resolve("demo.deck-voice").resolve(clip("Goodbye.")));

        final int made = DeckReader.speak(demo, List.of(voice.toString()));

        assertAll(
                () -> assertThat(made).isEqualTo(1),
                () -> assertThat(spokenParts()).isEqualTo(partsAfterFirst),
                () -> assertThat(media.resolve("demo.deck-voice").resolve(clip("Goodbye."))).exists());
    }

    @Test
    void theDeckSaysEachScenesNarration() {
        final Media demo = new Media(root, "demo");
        final Pronunciation pronunciation = DeckVoice.pronunciation(demo);
        final List<String> scenes = Narration.paragraphs(demo.file(".narration.md")).stream()
                .map(pronunciation::forSpeech).toList();

        assertThat(deckSays()).containsSubsequence(scenes);
    }

    @Test
    void theStoreReadsAClipItHasNotGotAndKeepsItForNextTime() throws IOException {
        final Media demo = new Media(root, "demo");
        final Spoken goodbye = new Spoken("Goodbye.", "Goodbye.");
        final WavFile first = VoiceClips.wav(demo, List.of(voice.toString()), goodbye);
        final long partsAfterFirst = spokenParts();

        final WavFile second = VoiceClips.wav(demo, List.of(voice.toString()), goodbye);

        assertAll(
                () -> assertThat(second.seconds()).isEqualTo(first.seconds()),
                () -> assertThat(spokenParts()).isEqualTo(partsAfterFirst).isEqualTo(1));
    }
}
