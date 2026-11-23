package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point: {@code VideoChecks <repository root>} fails if any lesson video script has a known mistake, or a deck
 * slide has no voice clip; it warns about clips no slide uses, and clips far longer or shorter than their words.
 */
@Slf4j
public final class VideoChecks {

    private VideoChecks() {
    }

    public static void main(final String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: VideoChecks <repository root>");
        }
        final Path root = Path.of(args[0]);
        final List<Problem> problems = new ArrayList<>(new VideoScriptChecker().check(root));
        // Where the voice can be run, a slide with no clip is a mistake; in a company's copy, a person records it.
        final boolean missingFails = Files.isDirectory(root.resolve("production/video-render"));
        final boolean removeUnused = Boolean.getBoolean("video.removeUnused");
        for (final Media media : SlideDeck.lessons(root, "all")) {
            final DeckClips.Findings clips = DeckClips.check(media, missingFails, removeUnused);
            problems.addAll(clips.problems());
            clips.warnings().forEach(warning -> log.warn("{}", oneLine(warning)));
            final String voices = DeckClips.voices(media);
            if (!voices.isEmpty()) {
                log.info("{}: deck voice: {}", media.safeLesson(), voices.replace('\r', ' ').replace('\n', ' '));
            }
        }
        problems.forEach(problem -> log.error("{}", oneLine(problem)));
        if (!problems.isEmpty()) {
            throw new IllegalStateException(problems.size() + " problem(s) in lesson video scripts");
        }
        log.info("Lesson video scripts are in order.");
    }

    private static String oneLine(final Problem problem) {
        return problem.toString().replace('\r', ' ').replace('\n', ' ');
    }
}
