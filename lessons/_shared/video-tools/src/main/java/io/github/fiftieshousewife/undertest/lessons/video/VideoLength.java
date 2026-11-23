package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * A lesson video's length, estimated before anything is recorded: each intro and closing slide for its narration's
 * spoken length plus the time it stays up afterwards, and the script's time on camera, with each live run's measured
 * time, or a guess for a run not yet timed. A video over ten minutes is too long.
 */
final class VideoLength {

    static final double LIMIT_SECONDS = 600;
    /** What a live run is taken to last until a recording has measured it. */
    static final double UNTIMED_RUN_SECONDS = 30;

    private VideoLength() {
    }

    static double estimate(final Media media) {
        final Slides slides = media.slides();
        double seconds = 0;
        for (final Slide slide : concat(slides.intro(), slides.closing())) {
            seconds += spoken(slide.narration()) + Slide.BREATH_SECONDS;
        }
        final List<Double> runs = new ArrayList<>(media.measuredRuns());
        runs.addAll(Collections.nCopies(waits(media.file(".tape")), UNTIMED_RUN_SECONDS));
        final List<Scene> scenes = TapeTimeline.scenes(media.root(), media.file(".tape"), runs);
        return seconds + (scenes.isEmpty() ? 0 : scenes.getLast().end());
    }

    /** A problem if the video would run over ten minutes, saying how long it would be. */
    static List<Problem> problems(final Media media) {
        if (!Files.isRegularFile(media.file(".tape"))) {
            return List.of(); // a shared script, not a lesson's
        }
        final double seconds;
        try {
            seconds = estimate(media);
        } catch (UncheckedIOException | IllegalArgumentException e) {
            return List.of(); // a missing Source file or a slides file that can't be read, which other checks report
        }
        if (seconds <= LIMIT_SECONDS) {
            return List.of();
        }
        final long whole = Math.round(seconds);
        return List.of(new Problem(media.file(".tape"), String.format(Locale.ROOT, "the video would run about %d min"
                + " %d s (slides, the recording, and %.0f s for each live run not yet timed); keep it under ten"
                + " minutes", whole / 60, whole % 60, UNTIMED_RUN_SECONDS)));
    }

    /** Seconds the voice takes to say a narration: its words at the voice's speed, and its pauses. */
    static double spoken(final String narration) {
        double pauses = 0;
        for (final Pauses.Part part : Pauses.parts(narration)) {
            pauses += part.pauseAfter();
        }
        final String words = Pauses.withoutMarkers(narration).strip();
        return (words.isEmpty() ? 0 : words.split("\\s+").length / LessonChecks.WORDS_PER_SECOND) + pauses;
    }

    /** How many {@code Wait} lines the script has: more than enough guesses for the runs not yet timed. */
    private static int waits(final Path tape) {
        try {
            return (int) Files.readAllLines(tape).stream().filter(line -> line.strip().startsWith("Wait")).count();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<Slide> concat(final List<Slide> first, final List<Slide> second) {
        final List<Slide> all = new ArrayList<>(first);
        all.addAll(second);
        return all;
    }
}
