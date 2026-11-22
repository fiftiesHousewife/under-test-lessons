package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A deck's voice: each slide's narration read aloud once and kept as a small sound file in {@code <id>.deck-voice/},
 * beside the deck, so the slides can be heard without making the video. A clip is named by its written words
 * ({@link Spoken#clipName}), so a deck plays only clips that match it and a person's recording survives a pronunciation
 * fix. Reading the clips aloud is {@code DeckReader}'s job, on the machine that makes the videos.
 */
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

    static Pronunciation pronunciation(final Media media) {
        return Pronunciation.read(media.root().resolve("lessons/_shared/pronunciations.txt"));
    }
}
