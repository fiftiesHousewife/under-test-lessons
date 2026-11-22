package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point: {@code VideoCheck <repository root>} checks every step a lesson video goes through, with the real
 * tools, in about half a minute, before any lesson is recorded. It records {@code video-check.tape} (code that rolls
 * in, a zoom on key lines, a callout that slides in) and checks the terminal reported where the zoomed lines landed;
 * draws a two-point slide stage by stage with the browser and builds it up; joins the slide and the zoomed recording;
 * and burns a caption in. It leaves {@code build/video-check/video-check.mp4} to watch.
 */
@Slf4j
public final class VideoCheck {

    static final Path TAPE = Path.of("production/video-render/video-check.tape");
    static final Path FOLDER = Path.of("build/video-check");
    /** The tape zooms on lines 22 to 24: three lines, two rows apart. */
    private static final int ROWS_APART = 2;
    private static final double SLIDE_VOICE_SECONDS = 4;

    private VideoCheck() {
    }

    public static void main(final String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: VideoCheck <repository root>");
        }
        final Path root = Path.of(args[0]);
        final Path folder = root.resolve(FOLDER);
        createFolder(folder);
        ExternalCommand.run(List.of(Recorder.vhs(), TAPE.toString()), root);
        final List<Optional<Zoom.Rows>> rows = Zoom.rows(folder.resolve("video-check.zooms"));
        final Optional<String> problem = problem(rows);
        if (problem.isPresent()) {
            throw new IllegalStateException("The zoom doesn't work in VHS's terminal: " + problem.get());
        }
        final Path zoomed = zoom(root, rows.getFirst().orElseThrow());
        final Path slide = slide(new Media(root, "mutation-testing"), folder);
        final Path joined = folder.resolve("video-check.joined.mp4");
        Splicer.join(List.of(slide, zoomed), joined);
        BurnedCaptions.burn(joined, List.of(new Cue(0.5, 3.5, "Slides build up one point at a time."),
                new Cue(6, 10, "Code rolls in, the key lines zoom, and a callout slides in.")),
                folder.resolve("video-check.mp4"));
        log.info("Every video step works. Watch build/video-check/video-check.mp4 to see them.");
    }

    /** The recording, zoomed on its key lines, with silent sound so it joins with slides. */
    private static Path zoom(final Path root, final Zoom.Rows rows) {
        final Zoom.Geometry geometry = Zoom.geometry(root.resolve(TAPE));
        final String filter = Zoom.filter(geometry, TapeTimeline.zooms(root, root.resolve(TAPE), List.of()),
                List.of(Zoom.window(geometry, rows)));
        final Path folder = root.resolve(FOLDER);
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error", "-i",
                "video-check.silent.mp4", "-f", "lavfi", "-i", "anullsrc=r=48000:cl=stereo", "-vf", filter,
                "-shortest", "-c:v", "libx264", "-pix_fmt", "yuv420p", "-c:a", "aac", "video-check.zoomed.mp4"),
                folder);
        return folder.resolve("video-check.zoomed.mp4");
    }

    /** A two-point slide, drawn stage by stage and built up over a silent voice. */
    private static Path slide(final Media media, final Path folder) {
        final Slide slide = new Slide(new Slide.Heading("What this checks", "checklist"),
                List.of("Slides build up one point at a time", "Code rolls in and zooms on the key lines"), "");
        final Path voice = folder.resolve("video-check.slide.wav");
        new WavFile(48_000, 1, new byte[(int) (48_000 * 2 * SLIDE_VOICE_SECONDS)]).write(voice);
        final Path video = folder.resolve("video-check.slide.mp4");
        ExternalCommand.run(Splicer.command(new Splicer.Cut(SlideDrawing.drawStages(media, "video-check", slide),
                voice, SLIDE_VOICE_SECONDS), video), folder);
        return video;
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
