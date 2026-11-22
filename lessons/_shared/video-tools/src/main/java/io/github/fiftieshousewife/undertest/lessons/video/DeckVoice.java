package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * A deck's voice: each slide's narration read aloud once and kept as a small sound file in {@code <id>.deck-voice/},
 * beside the deck, so the slides can be heard without making the video. A clip is named by what it says, so changed
 * narration gets a new clip, unchanged narration is never read again, and a deck plays only clips that match it.
 */
@Slf4j
final class DeckVoice {

    static final String FOLDER = ".deck-voice";
    private static final int NAME_LENGTH = 16;

    private DeckVoice() {
    }

    /** What the voice says on each slide of the deck, in deck order: intro slides, scenes, closing slides. */
    static List<String> spokenTexts(final Media media) {
        final Pronunciation pronunciation = pronunciation(media);
        final Slides slides = media.slides();
        final List<String> texts = new ArrayList<>();
        slides.intro().forEach(slide -> texts.add(slide.narration()));
        texts.addAll(SlideDeck.sceneNarrations(media));
        slides.closing().forEach(slide -> texts.add(slide.narration()));
        return texts.stream().map(pronunciation::forSpeech).toList();
    }

    /** The clip's file name: the start of the SHA-256 of what it says. */
    static String clipName(final String spokenText) {
        try {
            final byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(spokenText.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest).substring(0, NAME_LENGTH) + ".m4a";
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("every Java has SHA-256", e);
        }
    }

    /** For each slide, the clip's address relative to the deck, or empty when that slide has no clip yet. */
    static List<String> sources(final Media media) {
        final Path folder = media.file(FOLDER);
        return spokenTexts(media).stream().map(DeckVoice::clipName)
                .map(name -> Files.isRegularFile(folder.resolve(name)) ? media.name(FOLDER) + "/" + name : "")
                .toList();
    }

    /** Reads aloud each slide without a clip, removes clips no slide uses (leaving other files); counts those read. */
    static int speak(final Media media, final List<String> command) {
        final Path folder = media.file(FOLDER);
        final Path work = media.file(".voice").resolve("deck");
        createFolders(folder, work);
        final List<String> texts = spokenTexts(media);
        int read = 0;
        for (final String text : texts) {
            final Path clip = folder.resolve(clipName(text));
            if (!Files.isRegularFile(clip)) {
                final Path wav = work.resolve(clipName(text).replace(".m4a", ""));
                LessonVideo.speak(command, text, wav);
                compress(wav.resolveSibling(wav.getFileName() + ".wav"), clip);
                read++;
            }
        }
        removeUnused(folder, texts.stream().map(DeckVoice::clipName).collect(Collectors.toSet()));
        log.info("{}: deck voice has {} clips, {} read aloud now", media.safeLesson(), texts.size(), read);
        return read;
    }

    /** Speech compresses well: one channel of AAC at 48 kbit/s, about 6 KB a second. */
    private static void compress(final Path wav, final Path clip) {
        ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error", "-i", wav.toString(),
                "-ac", "1", "-c:a", "aac", "-b:a", "48k", clip.toString()), Path.of("").toAbsolutePath());
    }

    private static void removeUnused(final Path folder, final Set<String> used) {
        try (Stream<Path> clips = Files.list(folder)) {
            final List<Path> unused = clips.map(Path::getFileName).map(String::valueOf)
                    .filter(name -> name.endsWith(".m4a") && !used.contains(name)).map(folder::resolve).toList();
            for (final Path clip : unused) {
                Files.delete(clip);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Pronunciation pronunciation(final Media media) {
        return Pronunciation.read(media.root().resolve("lessons/_shared/pronunciations.txt"));
    }

    private static void createFolders(final Path... folders) {
        try {
            for (final Path folder : folders) {
                Files.createDirectories(folder);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
