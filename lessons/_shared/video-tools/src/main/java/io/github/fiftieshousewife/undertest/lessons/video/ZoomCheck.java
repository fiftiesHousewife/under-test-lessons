package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point: {@code ZoomCheck <repository root>} records {@code zoom-check.tape}, a few seconds of one zoomed code
 * panel, and checks the zoom works in VHS's terminal before any lesson is recorded: the helper reported real rows for
 * the zoomed lines, and the zoomed clip can be made. It leaves {@code build/zoom-check/zoom-check.zoomed.mp4} to watch.
 */
@Slf4j
public final class ZoomCheck {

    static final Path TAPE = Path.of("lessons/_shared/video-tools/zoom-check.tape");
    static final Path FOLDER = Path.of("build/zoom-check");
    /** The tape zooms on lines 22 to 24: three lines, two rows apart. */
    private static final int ROWS_APART = 2;

    private ZoomCheck() {
    }

    public static void main(final String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: ZoomCheck <repository root>");
        }
        final Path root = Path.of(args[0]);
        createFolder(root.resolve(FOLDER));
        ExternalCommand.run(List.of(Recorder.vhs(), TAPE.toString()), root);
        final List<Optional<Zoom.Rows>> rows = Zoom.rows(root.resolve(FOLDER).resolve("zoom-check.zooms"));
        final Optional<String> problem = problem(rows);
        if (problem.isPresent()) {
            throw new IllegalStateException("The zoom doesn't work in VHS's terminal: " + problem.get());
        }
        final Zoom.Geometry geometry = Zoom.geometry(root.resolve(TAPE));
        final String filter = Zoom.filter(geometry, TapeTimeline.zooms(root, root.resolve(TAPE), List.of()),
                List.of(Zoom.window(geometry, rows.getFirst().orElseThrow())));
        final Path folder = root.resolve(FOLDER);
        ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error",
                "-i", "zoom-check.silent.mp4", "-vf", filter, "-c:v", "libx264", "-pix_fmt", "yuv420p",
                "zoom-check.zoomed.mp4"), folder);
        log.info("The zoom works in VHS's terminal. Watch build/zoom-check/zoom-check.zoomed.mp4 to see it.");
    }

    /** What is wrong with the rows the check recording reported, if anything. */
    static Optional<String> problem(final List<Optional<Zoom.Rows>> rows) {
        if (rows.size() != 1) {
            return Optional.of("expected rows for 1 zoom, got " + rows.size()
                    + " (is VIDEO_ZOOMS set, and does show take --zoom?)");
        }
        if (rows.getFirst().isEmpty()) {
            return Optional.of("the terminal didn't say where the cursor was");
        }
        final Zoom.Rows found = rows.getFirst().get();
        if (found.last() - found.first() != ROWS_APART || found.first() < 1 || found.last() > found.screenRows()) {
            return Optional.of("rows " + found + " don't fit three zoomed lines on screen");
        }
        return Optional.empty();
    }

    private static void createFolder(final Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
