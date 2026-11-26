package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Records a lesson's changed live-run scenes (SceneChecksum) into <id>.deck-clips/scene-<n>.mp4, in one VHS session,
 * so Gradle warms up once. The session's run times place each scene exactly (TapeTimeline), and it is cut out: muted,
 * 1280 pixels wide, quick to start in a browser. The session's script goes to build/deck-clips.
 */
final class LiveRunClips {

    static final String CLIPS = ".deck-clips";
    private static final String SCENE_HEADER = "# ---- Scene";
    private static final String WIDTH = "1280";

    private LiveRunClips() {
    }

    static void record(final Media media) {
        final List<String> script = lines(media.file(".tape"));
        final List<List<String>> scenes = scenes(script);
        final Path clips = folder(media.file(CLIPS));
        final List<Integer> changed = changed(media, scenes, clips);
        if (changed.isEmpty()) {
            return;
        }
        final Path raw = clips.resolve("session.raw.mp4");
        final Path tape = sessionTape(media, settings(script), changed.stream().map(scenes::get).toList());
        new Recorder(Clock.systemUTC()).record(media, tape);
        final List<Scene> timed = TapeTimeline.scenes(media.root(), tape, media.measuredRuns());
        for (int i = 0; i < changed.size(); i++) {
            final int scene = changed.get(i);
            cut(raw, timed.get(i), clips.resolve(name(scene)));
            SceneChecksum.write(clips, scene, SceneChecksum.of(media, scenes.get(scene)));
        }
        delete(raw);
    }

    /* Writes the session's script, to build/deck-clips/<id>.tape, recording into the clips folder. */
    private static Path sessionTape(final Media media, final List<String> settings, final List<List<String>> chosen) {
        final Path raw = media.file(CLIPS).resolve("session.raw.mp4");
        final Path scripts = folder(media.root().resolve("build/deck-clips"));
        return write(scripts.resolve(media.safeLesson() + ".tape"),
                sessionScript(settings, chosen, media.root().relativize(raw)));
    }

    /* The live-run scenes whose clip is missing, or whose checksum no longer matches. */
    static List<Integer> changed(final Media media, final List<List<String>> scenes, final Path clips) {
        final List<Integer> changed = new ArrayList<>();
        for (int i = 0; i < scenes.size(); i++) {
            final boolean live = scenes.get(i).stream().anyMatch(line -> line.contains("run_live"));
            if (live && !SceneChecksum.matches(clips, i, SceneChecksum.of(media, scenes.get(i)))) {
                changed.add(i);
            }
        }
        return changed;
    }

    static String name(final int index) {
        return "scene-" + (index + 1) + ".mp4";
    }

    /* A script that records the given scenes in one session: the lesson's settings with one Output, then the scenes. */
    static List<String> sessionScript(final List<String> settings, final List<List<String>> scenes, final Path output) {
        final List<String> script = new ArrayList<>(List.of("Output " + output));
        settings.stream().filter(line -> !line.startsWith("Output ")).forEach(script::add);
        scenes.forEach(script::addAll);
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

    /* Cuts one scene out of the session's recording as a small clip. */
    static void cut(final Path raw, final Scene scene, final Path clip) {
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error", "-ss", seconds(scene.start()),
                "-t", seconds(scene.end() - scene.start()), "-i", raw.toString(), "-an",
                "-vf", "scale=" + WIDTH + ":-2", "-c:v", "libx264", "-preset", "slow", "-crf", "28",
                "-pix_fmt", "yuv420p", "-movflags", "+faststart", clip.toString()), Path.of("").toAbsolutePath());
    }

    private static String seconds(final double value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private static void delete(final Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new UncheckedIOException("could not remove " + file, e);
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
