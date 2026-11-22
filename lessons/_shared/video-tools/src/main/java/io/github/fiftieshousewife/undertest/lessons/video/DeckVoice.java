package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * A deck's voice: each slide's narration read aloud once and kept as a small sound file in {@code <id>.deck-voice/},
 * beside the deck, so the slides can be heard without making the video. A clip is named by its written words
 * ({@link Spoken#clipName}), so a deck plays only clips that match it and a person's recording survives a pronunciation
 * fix. {@link ClipLedger} records which clips the machine voice read, and from what: those are read again when what the
 * voice is given changes; a clip a person recorded is never read again or removed.
 */
@Slf4j
final class DeckVoice {

    static final String FOLDER = ".deck-voice";

    private DeckVoice() {
    }

    /** What each slide of the deck says, written and as the voice is given it, in deck order. */
    static List<Spoken> spoken(final Media media) {
        final Pronunciation pronunciation = pronunciation(media);
        final Slides slides = media.slides();
        final List<String> written = new ArrayList<>();
        slides.intro().forEach(slide -> written.add(slide.narration()));
        written.addAll(SlideDeck.sceneNarrations(media));
        slides.closing().forEach(slide -> written.add(slide.narration()));
        return written.stream().map(text -> Spoken.of(text, pronunciation)).toList();
    }

    /** For each slide, the clip's address relative to the deck, or empty when that slide has no clip yet. */
    static List<String> sources(final Media media) {
        final Path folder = media.file(FOLDER);
        return spoken(media).stream().map(Spoken::clipName)
                .map(name -> Files.isRegularFile(folder.resolve(name)) ? media.name(FOLDER) + "/" + name : "")
                .toList();
    }

    /**
     * Gives each slide its clip: a clip the voice read from what it would be given now, or a person's, is kept; one
     * still under its earlier name is renamed; the rest are made from the lesson's store of spoken clips
     * ({@link VoiceClips}), read aloud only if the video hasn't already. Removes clips the voice made that no slide
     * uses, never a person's. Returns how many were made.
     */
    static int speak(final Media media, final List<String> command) {
        final Path folder = media.file(FOLDER);
        createFolder(folder);
        final ClipLedger ledger = ClipLedger.read(folder);
        final List<Spoken> slides = spoken(media);
        int made = 0;
        for (final Spoken slide : slides) {
            if (!kept(folder, ledger, slide)) {
                VoiceClips.wav(media, command, slide);
                compress(VoiceClips.file(media, slide), folder.resolve(slide.clipName()));
                ledger.voiceRead(slide.clipName(), slide.saidKey());
                made++;
            }
        }
        removeUnused(folder, ledger, slides.stream().map(Spoken::clipName).collect(Collectors.toSet()));
        ledger.write();
        log.info("{}: deck voice has {} clips, {} made now", media.safeLesson(), slides.size(), made);
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

    /** Speech compresses well: one channel of AAC at 48 kbit/s, about 6 KB a second. */
    private static void compress(final Path wav, final Path clip) {
        ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error", "-i", wav.toString(),
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

    static Pronunciation pronunciation(final Media media) {
        return Pronunciation.read(media.root().resolve("lessons/_shared/pronunciations.txt"));
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
