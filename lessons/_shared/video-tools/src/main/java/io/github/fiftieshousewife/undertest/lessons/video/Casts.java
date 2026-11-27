package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/*
 * The live runs a deck plays as casts: each run's output with the time each line appeared, kept by
 * tools/capture-lesson-outputs.py as <id>.outputs/NN.cast and played by the asciinema player (Apache 2.0). The player
 * is kept once in the shared slides, in vendor/asciinema-player; PLAYER links its stylesheet and script from a deck's
 * folder, lessons/<id>/media, and publishing copies it beside the decks.
 */
final class Casts {

    static final String PLAYER = "<link rel=\"stylesheet\" href=\"../../_shared/slides/vendor/asciinema-player/"
            + "asciinema-player.css\"><script src=\"../../_shared/slides/vendor/asciinema-player/"
            + "asciinema-player.min.js\"></script>";

    private Casts() {
    }

    /* The lesson's live run with this number, from 1, as text to put in its deck, where it was kept as a cast. */
    static Optional<String> of(final Media media, final int run) {
        final Path cast = media.file(".outputs").resolve("%02d.cast".formatted(run));
        return Files.isRegularFile(cast) ? Optional.of(SlideHtml.read(cast)) : Optional.empty();
    }
}
