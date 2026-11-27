package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Entry point for a deck's voice, on the machine that voices the decks (building decks is {@code LessonDecks}'s):
 * {@code LessonDeckVoice <root> <id|all> <command...>} reads each deck slide's narration aloud with the given command
 * (which turns a text file into a WAV file) into {@code <id>.deck-voice/}, then rewrites the deck so it can play the
 * clips ({@link DeckVoice}).
 */
public final class LessonDeckVoice {

    private LessonDeckVoice() {
    }

    public static void main(final String... args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("usage: LessonDeckVoice <root> <lesson|all> <command...>; give the voice"
                    + " command: a program that turns a text file into a WAV");
        }
        final List<String> command = List.of(Arrays.copyOfRange(args, 2, args.length));
        Media.lessons(Path.of(args[0]), args[1]).forEach(media -> {
            DeckReader.speak(media, command);
            SlideDeck.write(media, false);
        });
    }
}
