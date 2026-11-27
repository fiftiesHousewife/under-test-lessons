package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point: {@code DeckChecks <repository root>} fails if any lesson's scene file, narration or slides has a known
 * mistake ({@link SceneFileChecker}), or a deck slide has no voice clip; it warns about clips no slide uses, and clips
 * far longer or shorter than their words.
 */
@Slf4j
public final class DeckChecks {

    private DeckChecks() {
    }

    public static void main(final String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: DeckChecks <repository root>");
        }
        final Path root = Path.of(args[0]);
        final List<Problem> problems = new ArrayList<>(new SceneFileChecker().check(root));
        problems.addAll(deckClips(root));
        problems.forEach(problem -> log.error("{}", oneLine(problem)));
        if (!problems.isEmpty()) {
            throw new IllegalStateException(problems.size() + " problem(s) in lesson decks");
        }
        log.info("Lesson decks are in order.");
    }

    /* Each deck's voice clips: problems returned, warnings and who voiced the deck logged. */
    private static List<Problem> deckClips(final Path root) {
        final List<Problem> problems = new ArrayList<>();
        // Where the voice can be run, a slide with no clip is a mistake; in a company's copy, a person records it.
        final boolean missingFails = Files.isDirectory(root.resolve("production/deck-voice"));
        final boolean removeUnused = Boolean.getBoolean("video.removeUnused");
        for (final Media media : Media.lessons(root, "all")) {
            final DeckClips.Findings clips = DeckClips.check(media, missingFails, removeUnused);
            problems.addAll(clips.problems());
            clips.warnings().forEach(warning -> log.warn("{}", oneLine(warning)));
            final String voices = DeckClips.voices(media);
            if (!voices.isEmpty()) {
                log.info("{}: deck voice: {}", media.safeLesson(), voices.replace('\r', ' ').replace('\n', ' '));
            }
        }
        return problems;
    }

    private static String oneLine(final Problem problem) {
        return problem.toString().replace('\r', ' ').replace('\n', ' ');
    }
}
