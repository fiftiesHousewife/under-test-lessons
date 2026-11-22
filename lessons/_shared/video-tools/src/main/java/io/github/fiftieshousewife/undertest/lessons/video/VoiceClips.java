package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * One store of spoken clips for a lesson's video and its deck, in {@code <id>.voice/clips/}, each named by what it says
 * (as {@link DeckVoice#clipName} names the deck's clips). The video and the deck say the same narration, so each piece
 * is read aloud once: whichever is voiced first reads it, and the other takes the clip from here. Where the store has
 * no clip but the deck does, as on a fresh clone, the deck's committed clip is decoded rather than read aloud again.
 */
final class VoiceClips {

    private static final String CLIPS = "clips";

    private VoiceClips() {
    }

    /** The clip of what is said: from the store, else decoded from the deck's clip, else read aloud now. */
    static WavFile wav(final Media media, final List<String> command, final String spokenText) {
        final Path wav = file(media, spokenText);
        if (Files.isRegularFile(wav)) {
            return WavFile.read(wav);
        }
        createFolder(folder(media));
        final Path deckClip = media.file(DeckVoice.FOLDER).resolve(DeckVoice.clipName(spokenText));
        if (Files.isRegularFile(deckClip)) {
            ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error", "-i",
                    deckClip.toString(), "-c:a", "pcm_s16le", wav.toString()), Path.of("").toAbsolutePath());
            return WavFile.read(wav);
        }
        return LessonVideo.speak(command, spokenText, folder(media).resolve(stem(spokenText)));
    }

    /** Where the store keeps the clip of what is said. */
    static Path file(final Media media, final String spokenText) {
        return folder(media).resolve(stem(spokenText) + ".wav");
    }

    private static Path folder(final Media media) {
        return media.file(".voice").resolve(CLIPS);
    }

    /** The clip's name without its ending: the same as the deck's clip of the same words. */
    private static String stem(final String spokenText) {
        return DeckVoice.clipName(spokenText).replace(".m4a", "");
    }

    private static void createFolder(final Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
