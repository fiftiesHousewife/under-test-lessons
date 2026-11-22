package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
        final Path deckClips = media.file(DeckVoice.FOLDER);
        final Path deckClip = deckClips.resolve(spoken.clipName());
        if (Files.isRegularFile(deckClip) && ClipLedger.read(deckClips).readFrom(spoken.clipName(), spoken.saidKey())) {
            ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error", "-i",
                    deckClip.toString(), "-c:a", "pcm_s16le", wav.toString()), Path.of("").toAbsolutePath());
            return WavFile.read(wav);
        }
        return LessonVideo.speak(command, spoken.said(), folder(media).resolve(spoken.saidKey()));
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
