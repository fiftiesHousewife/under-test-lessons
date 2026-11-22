package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for a lesson's slides and deck, which run anywhere the lessons do, inside a company included:
 * <ul>
 *   <li>{@code slides <root> <id|all>}: draws each slide's pictures with the installed browser;</li>
 *   <li>{@code deck <root> <id|all> [pdf]}: writes each deck, with the voice clips it has, and a PDF if asked.</li>
 * </ul>
 * Making videos and reading narration aloud is {@code LessonVideo}'s, on the machine that makes the videos.
 */
@Slf4j
public final class LessonDecks {

    private LessonDecks() {
    }

    public static void main(final String... args) {
        if (args.length < 3 || !("slides".equals(args[0]) || "deck".equals(args[0]))) {
            throw new IllegalArgumentException("usage: LessonDecks slides <root> <lesson|all>"
                    + " | deck <root> <lesson|all> [pdf]");
        }
        final Path root = Path.of(args[1]);
        if ("slides".equals(args[0])) {
            SlideDeck.lessons(root, args[2]).forEach(lesson -> log.info("{}: {} slides drawn, in the lesson's voice"
                    + " folder", lesson.safeLesson(), SlideDrawing.drawAll(lesson).size()));
            return;
        }
        final boolean pdf = args.length > 3 && "pdf".equals(args[3]);
        SlideDeck.lessons(root, args[2]).forEach(media -> SlideDeck.write(media, pdf));
    }
}
