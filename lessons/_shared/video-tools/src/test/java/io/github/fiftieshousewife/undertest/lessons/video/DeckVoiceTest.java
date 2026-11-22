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

/** Tests the deck's voice with stand-ins for the voice and ffmpeg. */
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
    }

    private Path standIn(final String name, final String body) throws IOException {
        final Path script = root.resolve(name);
        Files.writeString(script, "#!/bin/sh\n" + body + "\n");
        assertThat(script.toFile().setExecutable(true)).isTrue();
        return script;
    }

    private long spokenParts() throws IOException {
        final Path log = root.resolve("spoken.log");
        return Files.isRegularFile(log) ? Files.readAllLines(log).size() : 0;
    }

    @Test
    void speaksEachDeckSlideInOrderAsTheVoiceShouldSayIt() {
        assertThat(DeckVoice.spokenTexts(new Media(root, "demo")))
                .containsExactly("Hello there.", "Run pit now.", "Goodbye.");
    }

    @Test
    void namesEachClipByWhatItSays() {
        assertAll(
                () -> assertThat(DeckVoice.clipName("Hello there.")).isEqualTo(DeckVoice.clipName("Hello there."))
                        .matches("[0-9a-f]{16}\\.m4a"),
                () -> assertThat(DeckVoice.clipName("Hello there.")).isNotEqualTo(DeckVoice.clipName("Hello.")));
    }

    @Test
    void readsOnlySlidesWithoutAClipAndRemovesClipsNoSlideUses() throws IOException {
        final Media demo = new Media(root, "demo");
        final Path folder = media.resolve("demo.deck-voice");
        final int first = DeckVoice.speak(demo, List.of(voice.toString()));
        final long partsAfterFirst = spokenParts();
        Files.writeString(folder.resolve("0000000000000000.m4a"), "old");
        Files.writeString(folder.resolve("notes.txt"), "not a clip");

        final int second = DeckVoice.speak(demo, List.of(voice.toString()));

        assertAll(
                () -> assertThat(first).isEqualTo(3),
                () -> assertThat(second).isZero(),
                () -> assertThat(spokenParts()).isEqualTo(partsAfterFirst),
                () -> assertThat(folder.resolve(DeckVoice.clipName("Goodbye."))).exists(),
                () -> assertThat(folder.resolve("0000000000000000.m4a")).doesNotExist(),
                () -> assertThat(folder.resolve("notes.txt")).exists());
    }

    @Test
    void theDeckPlaysTheClipsItHasAndSaysNothingWhereItHasNone() throws IOException {
        final Path folder = Files.createDirectories(media.resolve("demo.deck-voice"));
        Files.writeString(folder.resolve(DeckVoice.clipName("Hello there.")), "sound");

        final String deck = SlideDeck.html(new Media(root, "demo"));

        assertAll(
                () -> assertThat(deck).containsOnlyOnce("<audio class=\"voice\"")
                        .contains("src=\"demo.deck-voice/" + DeckVoice.clipName("Hello there.") + "\""),
                () -> assertThat(DeckVoice.sources(new Media(root, "demo")))
                        .containsExactly("demo.deck-voice/" + DeckVoice.clipName("Hello there."), "", ""),
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
    void theVideoTakesTheClipsTheDeckHasAlreadyReadAloud() throws IOException {
        final Media demo = new Media(root, "demo");
        DeckVoice.speak(demo, List.of(voice.toString()));
        final long partsAfterDeck = spokenParts();

        DeckVoice.spokenTexts(demo).forEach(text -> VoiceClips.wav(demo, List.of(voice.toString()), text));

        assertThat(spokenParts()).isEqualTo(partsAfterDeck);
    }

    @Test
    void theDeckTakesTheClipsTheVideoHasAlreadyReadAloud() throws IOException {
        final Media demo = new Media(root, "demo");
        DeckVoice.spokenTexts(demo).forEach(text -> VoiceClips.wav(demo, List.of(voice.toString()), text));
        final long partsAfterVideo = spokenParts();

        final int made = DeckVoice.speak(demo, List.of(voice.toString()));

        assertAll(
                () -> assertThat(made).isEqualTo(3),
                () -> assertThat(spokenParts()).isEqualTo(partsAfterVideo),
                () -> assertThat(media.resolve("demo.deck-voice").resolve(DeckVoice.clipName("Goodbye."))).exists());
    }

    @Test
    void theVideoAndTheDeckSayTheSameSceneNarration() {
        final Media demo = new Media(root, "demo");
        final Pronunciation pronunciation = DeckVoice.pronunciation(demo);
        final List<String> video = Narration.paragraphs(demo.file(".narration.md")).stream()
                .map(pronunciation::forSpeech).toList();

        assertThat(DeckVoice.spokenTexts(demo)).containsSubsequence(video);
    }

    @Test
    void onAFreshCloneTheDecksClipIsDecodedRatherThanReadAloudAgain() throws IOException {
        final Media demo = new Media(root, "demo");
        final Path folder = Files.createDirectories(media.resolve("demo.deck-voice"));
        Files.writeString(folder.resolve(DeckVoice.clipName("Goodbye.")), "sound");
        System.setProperty("video.ffmpeg",
                standIn("decoder", "for last; do :; done; cp " + root.resolve("clip.wav") + " \"$last\"").toString());

        final WavFile clip = VoiceClips.wav(demo, List.of(voice.toString()), "Goodbye.");

        assertAll(
                () -> assertThat(spokenParts()).isZero(),
                () -> assertThat(clip.seconds()).isEqualTo(1.0));
    }
}
