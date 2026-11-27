package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * A lesson's store of spoken clips, in {@code <id>.voice/clips/} (never committed), each a WAV named by what the voice
 * is given and the engine that read it, so a piece of narration is read aloud once on a machine, and a deck clip lost
 * or removed is made again from it without reading it aloud.
 */
final class VoiceClips {

    private static final String CLIPS = "clips";

    private VoiceClips() {
    }

    /* The clip of what is said: from the store, else read aloud now. */
    static WavFile wav(final Media media, final List<String> command, final Spoken spoken) {
        final Path wav = file(media, spoken);
        if (Files.isRegularFile(wav)) {
            return WavFile.read(wav);
        }
        createFolder(folder(media));
        return Speech.readAll(command, List.of(Speech.of(spoken.said(), folder(media).resolve(key(spoken)))))
                .getFirst();
    }

    /*
     * Has the voice read, in one go, every piece the store has no clip of: a voice that reads lists then loads its
     * model once for the lesson, not once a clip. {@link #wav} then finds each in the store.
     */
    static void readAhead(final Media media, final List<String> command, final List<Spoken> pieces) {
        final Map<String, Spoken> unread = new LinkedHashMap<>();
        pieces.stream().filter(piece -> !Files.isRegularFile(file(media, piece)))
                .forEach(piece -> unread.putIfAbsent(key(piece), piece));
        if (unread.isEmpty()) {
            return;
        }
        createFolder(folder(media));
        Speech.readAll(command, unread.values().stream()
                .map(piece -> Speech.of(piece.said(), folder(media).resolve(key(piece)))).toList());
    }

    /* Where the store keeps the clip of what is said. */
    static Path file(final Media media, final Spoken spoken) {
        return folder(media).resolve(key(spoken) + ".wav");
    }

    /*
     * The store's name for a clip: what the voice is given, and the engine that read it (see {@link Speech#engine}), so
     * a new engine never takes an old engine's clip.
     */
    private static String key(final Spoken spoken) {
        return spoken.saidKey() + "-" + Integer.toHexString(Speech.engine().hashCode());
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
