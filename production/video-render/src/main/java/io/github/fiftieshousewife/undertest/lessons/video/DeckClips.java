package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/*
 * Cuts each live-run scene out of a lesson's silent recording, into <id>.deck-clips/scene-<n>.mp4, so the deck can play
 * the run as it really looked. Each clip is muted, 1280 pixels wide, and starts at once in a browser (faststart). A
 * scene with no live command gets no clip, and a deck shows the run's kept output where there is none.
 */
final class DeckClips {

    static final String CLIPS = ".deck-clips";
    private static final String WIDTH = "1280";

    private DeckClips() {
    }

    static void cut(final Media media) {
        final Path recording = recording(media);
        final List<Scene> scenes = Recording.scenes(media);
        final List<SceneContent> contents = SceneContent.read(media.root(), media.file(".tape"));
        final Path folder = folder(media);
        for (int i = 0; i < scenes.size() && i < contents.size(); i++) {
            if (!contents.get(i).commands().isEmpty()) {
                cutOne(recording, scenes.get(i), folder.resolve(name(i)));
            }
        }
    }

    private static Path recording(final Media media) {
        final Path recording = media.file(".silent.mp4");
        if (!Files.isRegularFile(recording)) {
            throw new IllegalStateException("record the lesson first: no " + recording);
        }
        return recording;
    }

    /* The clip's file name for the scene at this index, counting scenes from 1. */
    static String name(final int index) {
        return "scene-" + (index + 1) + ".mp4";
    }

    private static Path folder(final Media media) {
        final Path folder = media.file(CLIPS);
        try {
            return Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException("could not make " + folder, e);
        }
    }

    private static void cutOne(final Path recording, final Scene scene, final Path clip) {
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error",
                "-ss", seconds(scene.start()), "-to", seconds(scene.end()), "-i", recording.toString(), "-an",
                "-vf", "scale=" + WIDTH + ":-2", "-c:v", "libx264", "-preset", "slow", "-crf", "28",
                "-pix_fmt", "yuv420p", "-movflags", "+faststart", clip.toString()), Path.of("").toAbsolutePath());
    }

    private static String seconds(final double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
