package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One store of spoken clips for a lesson's video and its deck, in {@code <id>.voice/clips/}, each named by what the
 * voice is given. The video and the deck say the same narration, so each piece is read aloud once: whichever is voiced
 * first reads it, and the other takes the clip from here. Where the store has no clip but the deck has one the voice
 * read from the same words, as on a fresh clone, the deck's committed clip is decoded rather than read aloud again.
 */
final class VoiceClips {

    private static final String CLIPS = "clips";

    private VoiceClips() {
    }

    /**
     * The clip of what is said: from the store, else decoded from the deck's clip if the voice read it from the same
     * words, else read aloud now.
     */
    static WavFile wav(final Media media, final List<String> command, final Spoken spoken) {
        final Path wav = file(media, spoken);
        if (Files.isRegularFile(wav)) {
            return WavFile.read(wav);
        }
        createFolder(folder(media));
        if (decodable(media, spoken)) {
            ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error", "-i",
                    media.file(DeckVoice.FOLDER).resolve(spoken.clipName()).toString(), "-c:a", "pcm_s16le",
                    wav.toString()), Path.of("").toAbsolutePath());
            return WavFile.read(wav);
        }
        return LessonVideo.speak(command, spoken.said(), folder(media).resolve(spoken.saidKey()));
    }

    /**
     * Has the voice read, in one go, every piece the store has no clip of and the deck can't give: a voice that reads
     * lists then loads its model once for the lesson, not once a clip. {@link #wav} then finds each in the store.
     */
    static void readAhead(final Media media, final List<String> command, final List<Spoken> pieces) {
        final Map<String, Spoken> unread = new LinkedHashMap<>();
        pieces.stream().filter(piece -> !Files.isRegularFile(file(media, piece)) && !decodable(media, piece))
                .forEach(piece -> unread.putIfAbsent(piece.saidKey(), piece));
        if (unread.isEmpty()) {
            return;
        }
        createFolder(folder(media));
        Speech.readAll(command, unread.values().stream()
                .map(piece -> Speech.of(piece.said(), folder(media).resolve(piece.saidKey()))).toList());
    }

    /** Whether the deck has a clip the voice read from the same words, to decode rather than read again. */
    private static boolean decodable(final Media media, final Spoken spoken) {
        final Path deckClips = media.file(DeckVoice.FOLDER);
        return Files.isRegularFile(deckClips.resolve(spoken.clipName()))
                && ClipLedger.read(deckClips).readFrom(spoken.clipName(), spoken.saidKey());
    }

    /** Where the store keeps the clip of what is said. */
    static Path file(final Media media, final Spoken spoken) {
        return folder(media).resolve(spoken.saidKey() + ".wav");
    }

    private static Path folder(final Media media) {
        return media.file(".voice").resolve(CLIPS);
    }

    private static void createFolder(final Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
