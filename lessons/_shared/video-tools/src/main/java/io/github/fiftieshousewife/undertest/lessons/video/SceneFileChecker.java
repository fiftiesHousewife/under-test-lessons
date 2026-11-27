package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Checks each lesson's scene file ({@code <id>.scenes}) and what its deck is built from with it:
 * <ul>
 *   <li>one narration item a scene, and narration the voice can read (no commands, no semicolons);</li>
 *   <li>code panels that find their file, and line ranges that fit inside it;</li>
 *   <li>live commands with their output kept, fitting one screen, and lines the scene names shown marked;</li>
 *   <li>phrases the slides and narration never use ({@link DontSay});</li>
 *   <li>README commands that say what their kept output bears out ({@link ReadmeCommands});</li>
 *   <li>the slides file ({@link SlideFileChecks}), for lessons made of slides alone too.</li>
 * </ul>
 */
public final class SceneFileChecker {

    /** Checks every lesson under {@code lessons/}; paths in scene files are relative to {@code root}. */
    public List<Problem> check(final Path root) {
        final List<Problem> problems = new ArrayList<>();
        for (final Path folder : lessonFolders(root)) {
            final Media media = new Media(root, String.valueOf(folder.getFileName()));
            if (Files.isRegularFile(media.scenes())) {
                problems.addAll(checkLesson(media));
            } else {
                slides(media).ifPresent(slides -> problems.addAll(SlideFileChecks.slides(slides)));
            }
        }
        return problems;
    }

    private static List<Problem> checkLesson(final Media media) {
        final List<Problem> problems = new ArrayList<>(narrationProblems(media));
        problems.addAll(LessonChecks.shows(media.root(), media.scenes()));
        problems.addAll(LiveOutputs.problems(media));
        problems.addAll(DontSay.problems(media));
        problems.addAll(ReadmeCommands.problems(media));
        slides(media).ifPresent(slides -> problems.addAll(SlideFileChecks.slides(slides)));
        return problems;
    }

    private static List<Problem> narrationProblems(final Media media) {
        final Path narration = media.file(".narration.md");
        if (!Files.isRegularFile(narration)) {
            return List.of();
        }
        final List<Problem> problems = new ArrayList<>(Narration.checkNarration(narration));
        Narration.checkSceneCount(media.scenes(), narration).ifPresent(problems::add);
        return problems;
    }

    private static Optional<Path> slides(final Media media) {
        final Path slides = media.file(".slides.md");
        return Files.isRegularFile(slides) ? Optional.of(slides) : Optional.empty();
    }

    /* Each folder under {@code lessons/}, in name order. */
    private static List<Path> lessonFolders(final Path root) {
        final Path lessons = root.resolve("lessons");
        if (!Files.isDirectory(lessons)) {
            return List.of();
        }
        try (Stream<Path> folders = Files.list(lessons)) {
            return folders.filter(Files::isDirectory).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
