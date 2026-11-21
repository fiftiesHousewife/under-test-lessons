package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point: {@code Captions <repository root> [lesson id]} writes {@code <id>.vtt} and {@code <id>.srt} next to each
 * lesson's video script, from its narration file. With no lesson id, it does every lesson that has a narration file.
 */
@Slf4j
public final class Captions {

    private Captions() {
    }

    public static void main(final String... args) {
        if (args.length < 1 || args.length > 2) {
            throw new IllegalArgumentException("usage: Captions <repository root> [lesson id]");
        }
        final Path root = Path.of(args[0]);
        final List<String> lessons = args.length == 2 ? List.of(args[1]) : lessonsWithNarration(root);
        lessons.forEach(lesson -> write(root, lesson));
    }

    static void write(final Path root, final String lesson) {
        final Media media = new Media(root, lesson);
        final List<Scene> scenes = media.scenes();
        final List<Cue> cues = CaptionFile.cues(scenes, Narration.paragraphs(media.file(".narration.md")));
        writeFile(media.file(".vtt"), CaptionFile.webVtt(cues));
        writeFile(media.file(".srt"), CaptionFile.srt(cues));
        log.info("{}: {} scenes, {} captions, {} seconds", media.safeLesson(), scenes.size(), cues.size(),
                Math.round(scenes.getLast().end()));
    }

    static List<String> lessonsWithNarration(final Path root) {
        try (Stream<Path> dirs = Files.list(root.resolve("lessons"))) {
            return dirs.map(dir -> String.valueOf(dir.getFileName()))
                    .filter(id -> Files.isRegularFile(root.resolve("lessons/" + id + "/media/" + id + ".narration.md")))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeFile(final Path file, final String text) {
        try {
            Files.writeString(file, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
