package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

/*
 * Records each live-run scene of a lesson on its own, into <id>.deck-clips/scene-<n>.mp4, so the deck can play the run
 * as it really looked. Each scene gets a short script: the lesson script's settings, then that scene alone, so the clip
 * starts and ends exactly at the scene, and a lesson's clips take minutes rather than its whole video. Each clip is
 * then made muted, 1280 pixels wide, and quick to start in a browser (faststart). The scripts go to build/deck-clips.
 */
final class LiveRunClips {

    static final String CLIPS = ".deck-clips";
    private static final String SCENE_HEADER = "# ---- Scene";
    private static final String WIDTH = "1280";

    private LiveRunClips() {
    }

    static void record(final Media media) {
        final List<String> script = lines(media.file(".tape"));
        final List<String> settings = settings(script);
        final List<List<String>> scenes = scenes(script);
        final Path clips = folder(media.file(CLIPS));
        final Path scripts = folder(media.root().resolve("build/deck-clips"));
        for (int i = 0; i < scenes.size(); i++) {
            if (scenes.get(i).stream().anyMatch(line -> line.contains("run_live"))) {
                final Path raw = clips.resolve("scene-" + (i + 1) + ".raw.mp4");
                final Path tape = write(scripts.resolve(media.safeLesson() + "-scene-" + (i + 1) + ".tape"),
                        clipScript(settings, scenes.get(i), media.root().relativize(raw)));
                new Recorder(Clock.systemUTC()).record(media, tape);
                shrink(raw, clips.resolve(name(i)));
            }
        }
    }

    /* The clip's file name for the scene at this index, counting scenes from 1. */
    static String name(final int index) {
        return "scene-" + (index + 1) + ".mp4";
    }

    /* A script that records one scene: the lesson's settings with one Output, the clip's, then the scene. */
    static List<String> clipScript(final List<String> settings, final List<String> scene, final Path output) {
        final List<String> script = new ArrayList<>(List.of("Output " + output));
        settings.stream().filter(line -> !line.startsWith("Output ")).forEach(script::add);
        script.addAll(scene);
        return script;
    }

    /* Everything before the first scene: the outputs, settings and set-up every scene relies on. */
    static List<String> settings(final List<String> script) {
        final int first = firstScene(script);
        return script.subList(0, first < 0 ? script.size() : first);
    }

    /* Each scene's lines, from its header to the next. */
    static List<List<String>> scenes(final List<String> script) {
        final List<List<String>> scenes = new ArrayList<>();
        for (final String line : script.subList(Math.max(0, firstScene(script)), script.size())) {
            if (line.startsWith(SCENE_HEADER)) {
                scenes.add(new ArrayList<>());
            }
            if (!scenes.isEmpty()) {
                scenes.getLast().add(line);
            }
        }
        return scenes;
    }

    private static int firstScene(final List<String> script) {
        for (int i = 0; i < script.size(); i++) {
            if (script.get(i).startsWith(SCENE_HEADER)) {
                return i;
            }
        }
        return -1;
    }

    /* Makes the recorded scene a small clip, and removes the full-size recording. */
    static void shrink(final Path raw, final Path clip) {
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error", "-i", raw.toString(), "-an",
                "-vf", "scale=" + WIDTH + ":-2", "-c:v", "libx264", "-preset", "slow", "-crf", "28",
                "-pix_fmt", "yuv420p", "-movflags", "+faststart", clip.toString()), Path.of("").toAbsolutePath());
        try {
            Files.deleteIfExists(raw);
        } catch (IOException e) {
            throw new UncheckedIOException("could not remove " + raw, e);
        }
    }

    private static Path folder(final Path folder) {
        try {
            return Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException("could not make " + folder, e);
        }
    }

    private static Path write(final Path file, final List<String> lines) {
        try {
            return Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException("could not write " + file, e);
        }
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException("could not read " + file, e);
        }
    }
}
