package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Reads a deck's slides aloud, on the machine that makes the videos: each slide's clip, in {@code <id>.deck-voice/},
 * named as {@link DeckVoice} names it. {@link ClipLedger} records which clips the machine voice read, and from what:
 * those are read again when what the voice is given changes; a clip a person recorded is never read again or removed.
 */
@Slf4j
final class DeckReader {

    private DeckReader() {
    }

    /**
     * Gives each slide its clip: a clip the voice read from what it would be given now, or a person's, is kept; one
     * still under its earlier name is renamed; the rest are made from the lesson's store of spoken clips
     * ({@link VoiceClips}), read aloud only if the video hasn't already. Removes clips the voice made that no slide
     * uses, never a person's. Returns how many were made.
     */
    static int speak(final Media media, final List<String> command) {
        final Path folder = media.file(DeckVoice.FOLDER);
        createFolder(folder);
        final ClipLedger ledger = ClipLedger.read(folder);
        final List<Spoken> slides = DeckVoice.spoken(media);
        final List<Spoken> needed = slides.stream().filter(slide -> !kept(folder, ledger, slide)).toList();
        VoiceClips.readAhead(media, command, needed);
        for (final Spoken slide : needed) {
            VoiceClips.wav(media, command, slide);
            compress(VoiceClips.file(media, slide), folder.resolve(slide.clipName()));
            ledger.voiceRead(slide.clipName(), slide.saidKey());
        }
        final int made = needed.size();
        removeUnused(folder, ledger, slides.stream().map(Spoken::clipName).collect(Collectors.toSet()));
        ledger.write();
        log.info("{}: deck voice has {} clips, {} made now", LogSafe.lesson(media), slides.size(), made);
        return made;
    }

    /** Whether the slide already has the clip it needs, renaming one still under its earlier name. */
    private static boolean kept(final Path folder, final ClipLedger ledger, final Spoken slide) {
        final Path clip = folder.resolve(slide.clipName());
        if (Files.isRegularFile(clip) && ledger.first() && slide.clipName().equals(slide.earlierClipName())) {
            ledger.voiceRead(slide.clipName(), slide.saidKey()); // made before ledgers, by the voice, from these words
            return true;
        }
        if (Files.isRegularFile(clip)) {
            return !ledger.byTheVoice(slide.clipName()) || ledger.readFrom(slide.clipName(), slide.saidKey());
        }
        final Path earlier = folder.resolve(slide.earlierClipName());
        if (Files.isRegularFile(earlier)) {
            move(earlier, clip);
            ledger.forget(slide.earlierClipName());
            ledger.voiceRead(slide.clipName(), slide.saidKey());
            return true;
        }
        return false;
    }

    /**
     * Speech compresses well: one channel of AAC at 48 kbit/s, about 6 KB a second. The clip is the video's sound as
     * it is, its gaps between sentences already shortened ({@link Speech#LONGEST_GAP}), so a fresh clone's video
     * decoded from it sounds the same.
     */
    private static void compress(final Path wav, final Path clip) {
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error", "-i", wav.toString(),
                "-ac", "1", "-c:a", "aac", "-b:a", "48k", clip.toString()), Path.of("").toAbsolutePath());
    }

    private static void removeUnused(final Path folder, final ClipLedger ledger, final Set<String> used) {
        try (Stream<Path> clips = Files.list(folder)) {
            final List<String> unused = clips.map(Path::getFileName).map(String::valueOf)
                    .filter(name -> name.endsWith(".m4a") && !used.contains(name) && ledger.byTheVoice(name)).toList();
            for (final String clip : unused) {
                Files.delete(folder.resolve(clip));
                ledger.forget(clip);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void move(final Path from, final Path to) {
        try {
            Files.move(from, to);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void createFolder(final Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
