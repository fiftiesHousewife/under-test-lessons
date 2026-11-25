package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/* Tests the deck's voice with stand-ins for the voice and ffmpeg. */
class DeckVoiceTest {

    @TempDir
    Path root;

    private Path media;
    private Path voice;

    @BeforeEach
    void lesson() throws IOException {
        media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.writeString(media.resolve("demo.tape"), "# ---- Scene 1: one ----\nSleep 3s\n");
        Files.writeString(media.resolve("demo.narration.md"), "1. Run PIT now.\n");
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\nSay: Hello there.\n"
                + "## closing: Bye\n- A point\nSay: Goodbye.\n");
        final Path shared = Files.createDirectories(root.resolve("lessons/_shared/slides/pictures"));
        Files.writeString(shared.resolveSibling("slides.css"), "body { }");
        Files.writeString(shared.resolveSibling("deck.css"), ".deck { }");
        Files.writeString(shared.resolveSibling("deck.js"), "// controls");
        Files.writeString(root.resolve("lessons/_shared/pronunciations.txt"), "PIT = pit\n");
        new WavFile(100, 1, new byte[200]).write(root.resolve("clip.wav"));
        voice = standIn("voice", "echo \"$1\" >> " + root.resolve("spoken.log") + "; cp " + root.resolve("clip.wav")
                + " \"$2\"");
        System.setProperty("video.ffmpeg", standIn("ffmpeg", "for last; do :; done; touch \"$last\"").toString());
    }

    @AfterEach
    void restore() {
        System.clearProperty("video.ffmpeg");
        System.clearProperty(Speech.READS_LISTS);
        System.clearProperty(Speech.ENGINE_PROPERTY);
    }

    private Path standIn(final String name, final String body) throws IOException {
        final Path script = root.resolve(name);
        Files.writeString(script, "#!/bin/sh\n" + body + "\n");
        assertThat(script.toFile().setExecutable(true)).isTrue();
        return script;
    }

    private static String clip(final String written) {
        return new Spoken(written, written).clipName();
    }

    private List<String> deckSays() {
        return DeckVoice.spoken(new Media(root, "demo")).stream().map(Spoken::said).toList();
    }

    private long spokenParts() throws IOException {
        final Path log = root.resolve("spoken.log");
        return Files.isRegularFile(log) ? Files.readAllLines(log).size() : 0;
    }

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
                () -> assertThat(SlideDeck.withVoice("<section>x</section>", "")).isEqualTo("<section>x</section>"));
    }

    @Test
    void theCommandReadsTheDeckAloudAndRewritesTheDeck() {
        LessonVideo.main("deckvoice", root.toString(), "demo", voice.toString());

        assertAll(
                () -> assertThat(Files.readString(media.resolve("demo.deck.html"))).contains("<audio class=\"voice\""),
                () -> assertThatIllegalArgumentException()
                        .isThrownBy(() -> LessonVideo.main("deckvoice", root.toString(), "demo")));
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
    void theVideoTakesTheClipsTheDeckHasAlreadyReadAloud() throws IOException {
        final Media demo = new Media(root, "demo");
        DeckReader.speak(demo, List.of(voice.toString()));
        final long partsAfterDeck = spokenParts();

        DeckVoice.spoken(demo).forEach(slide -> VoiceClips.wav(demo, List.of(voice.toString()), slide));

        assertThat(spokenParts()).isEqualTo(partsAfterDeck);
    }

    @Test
    void theDeckTakesTheClipsTheVideoHasAlreadyReadAloud() throws IOException {
        final Media demo = new Media(root, "demo");
        DeckVoice.spoken(demo).forEach(slide -> VoiceClips.wav(demo, List.of(voice.toString()), slide));
        final long partsAfterVideo = spokenParts();

        final int made = DeckReader.speak(demo, List.of(voice.toString()));

        assertAll(
                () -> assertThat(made).isEqualTo(3),
                () -> assertThat(spokenParts()).isEqualTo(partsAfterVideo),
                () -> assertThat(media.resolve("demo.deck-voice").resolve(clip("Goodbye."))).exists());
    }

    @Test
    void theVideoAndTheDeckSayTheSameSceneNarration() {
        final Media demo = new Media(root, "demo");
        final Pronunciation pronunciation = DeckVoice.pronunciation(demo);
        final List<String> video = Narration.paragraphs(demo.file(".narration.md")).stream()
                .map(pronunciation::forSpeech).toList();

        assertThat(deckSays()).containsSubsequence(video);
    }

    @Test
    void onAFreshCloneTheDecksClipIsDecodedRatherThanReadAloudAgain() throws IOException {
        final Media demo = new Media(root, "demo");
        DeckReader.speak(demo, List.of(voice.toString()));
        final long partsAfterDeck = spokenParts();
        deleteStore();
        System.setProperty("video.ffmpeg",
                standIn("decoder", "for last; do :; done; cp " + root.resolve("clip.wav") + " \"$last\"").toString());

        final WavFile clip = VoiceClips.wav(demo, List.of(voice.toString()), new Spoken("Goodbye.", "Goodbye."));

        assertAll(
                () -> assertThat(spokenParts()).isEqualTo(partsAfterDeck),
                () -> assertThat(clip.seconds()).isEqualTo(1.0));
    }

    private void deleteStore() throws IOException {
        try (var files = Files.walk(media.resolve("demo.voice"))) {
            for (final Path file : files.sorted(java.util.Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }
}
