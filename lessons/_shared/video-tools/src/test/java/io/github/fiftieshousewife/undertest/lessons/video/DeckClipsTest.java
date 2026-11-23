package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DeckClipsTest {

    private static final String HELLO = "Hello there, and welcome to a lesson about old code.";
    private static final String BYE = "Goodbye.";

    @TempDir
    Path root;

    private Path clips;

    @BeforeEach
    void lesson() throws IOException {
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\nSay: " + HELLO + "\n"
                + "## closing: Bye\n- A point\nSay: " + BYE + "\n");
        clips = Files.createDirectories(media.resolve("demo.deck-voice"));
    }

    private Path clip(final String written, final double seconds) throws IOException {
        return Files.write(clips.resolve(new Spoken(written, written).clipName()), mp4(seconds));
    }

    private DeckClips.Findings check(final boolean missingFails, final boolean removeUnused) {
        return DeckClips.check(new Media(root, "demo"), missingFails, removeUnused);
    }

    @Test
    void aSlideWithNoClipFailsWhereTheVoiceRunsAndIsAWarningInACompanysCopy() throws IOException {
        clip(HELLO, 3);

        assertAll(
                () -> assertThat(check(true, false).problems()).extracting(Problem::message).containsExactly(
                        "slide 2 (\"Goodbye.\") has no clip " + new Spoken(BYE, BYE).clipName()
                                + "; voice the deck, or record it"),
                () -> assertThat(check(false, false).problems()).isEmpty(),
                () -> assertThat(check(false, false).warnings()).hasSize(1));
    }

    @Test
    void listsAClipNoSlideUsesAndRemovesItOnlyWhenAsked() throws IOException {
        clip(HELLO, 3);
        clip(BYE, 0.5);
        final Path old = clip("Hello, an older wording of the first slide.", 3);
        Files.writeString(clips.resolve(ClipLedger.FILE), old.getFileName() + " voice abc\n");

        final DeckClips.Findings listed = check(true, false);
        final boolean keptUntilAsked = Files.exists(old);
        check(true, true);

        assertAll(
                () -> assertThat(listed.warnings()).extracting(Problem::message)
                        .containsExactly("no slide uses this clip; remove it with -PremoveUnused=true"),
                () -> assertThat(keptUntilAsked).isTrue(),
                () -> assertThat(old).doesNotExist(),
                () -> assertThat(Files.readString(clips.resolve(ClipLedger.FILE))).doesNotContain("abc"));
    }

    @Test
    void warnsOfAClipFarLongerOrShorterThanItsWords() throws IOException {
        clip(HELLO, 1);
        clip(BYE, 0.5);

        assertThat(check(true, false).warnings()).extracting(Problem::message).containsExactly(
                "slide 1 (\"Hello there, and welcome to a…\") takes 1.0 s, but its words take about 3.8 s to say;"
                        + " check it says the slide's words, whole");
    }

    @Test
    void readsAClipsLengthFromItsMp4Header() throws IOException {
        assertThat(DeckClips.Mp4Length.seconds(clip(BYE, 2.5))).isCloseTo(2.5, within(1e-9));
    }

    @Test
    void aPersonsRecordingMayBeAWavOrAnMp3AndTheDeckPlaysIt() throws IOException {
        final Path wav = Files.write(clips.resolve(new Spoken(HELLO, HELLO).clipStem() + ".wav"), wav(3));
        Files.write(clips.resolve(new Spoken(BYE, BYE).clipStem() + ".mp3"), new byte[] {1, 2, 3});

        assertAll(
                () -> assertThat(check(true, false).problems()).isEmpty(),
                () -> assertThat(check(true, false).warnings()).isEmpty(),
                () -> assertThat(DeckClips.WavLength.seconds(wav)).isCloseTo(3, within(1e-9)),
                () -> assertThat(DeckVoice.sources(new Media(root, "demo"))).containsExactly(
                        "demo.deck-voice/" + wav.getFileName(),
                        "demo.deck-voice/" + new Spoken(BYE, BYE).clipStem() + ".mp3"));
    }

    @Test
    void warnsOfASlideWithClipsInTwoFormats() throws IOException {
        clip(HELLO, 3);
        Files.write(clips.resolve(new Spoken(HELLO, HELLO).clipStem() + ".wav"), wav(3));
        clip(BYE, 0.5);

        assertThat(check(true, false).warnings()).extracting(Problem::message).containsExactly(
                "slide 1 has 2 clips, " + new Spoken(HELLO, HELLO).clipName() + " and "
                        + new Spoken(HELLO, HELLO).clipStem() + ".wav; the deck plays "
                        + new Spoken(HELLO, HELLO).clipName() + ", so remove the other");
    }

    /** A silent WAV: 8000 one-channel 16-bit samples a second. */
    private static byte[] wav(final double seconds) {
        final int size = (int) Math.round(seconds * 16000);
        final ByteBuffer out = ByteBuffer.allocate(44 + size).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        out.put("RIFF".getBytes(StandardCharsets.US_ASCII)).putInt(36 + size).put("WAVE".getBytes(StandardCharsets.US_ASCII))
                .put("fmt ".getBytes(StandardCharsets.US_ASCII)).putInt(16).putShort((short) 1).putShort((short) 1)
                .putInt(8000).putInt(16000).putShort((short) 2).putShort((short) 16)
                .put("data".getBytes(StandardCharsets.US_ASCII)).putInt(size);
        return out.array();
    }

    /** The smallest MP4 the check reads: an ftyp box, then moov holding mvhd with a timescale of 1000. */
    private static byte[] mp4(final double seconds) {
        final ByteBuffer out = ByteBuffer.allocate(16 + 8 + 28);
        out.putInt(16).put("ftyp".getBytes(StandardCharsets.US_ASCII)).put("M4A ".getBytes(StandardCharsets.US_ASCII))
                .putInt(0);
        out.putInt(8 + 28).put("moov".getBytes(StandardCharsets.US_ASCII));
        out.putInt(28).put("mvhd".getBytes(StandardCharsets.US_ASCII)).putInt(0).putInt(0).putInt(0).putInt(1000)
                .putInt((int) Math.round(seconds * 1000));
        return out.array();
    }
}
