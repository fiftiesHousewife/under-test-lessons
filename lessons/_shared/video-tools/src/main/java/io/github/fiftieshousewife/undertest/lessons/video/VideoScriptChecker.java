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
 * Checks lesson video scripts (VHS {@code .tape} files) for mistakes that otherwise only show up as a recording that
 * hangs until it times out:
 * <ul>
 *   <li>a {@code Wait} that looks only at the last line, which is usually the next prompt by the time output appears;
 *   </li>
 *   <li>a {@code Wait} with no pattern, which waits for a prompt shape our scripts don't use;</li>
 *   <li>a {@code Wait} pattern that already matches text typed earlier, so it stops waiting at once;</li>
 *   <li>a Gradle command piped into {@code grep} without {@code --rerun}: a task skipped as up to date prints
 *   nothing;</li>
 *   <li>a {@code Source} file that doesn't exist.</li>
 * </ul>
 */
public final class VideoScriptChecker {


    /** Checks every {@code .tape} file under {@code lessons/}; paths in scripts are relative to {@code root}. */
    public List<Problem> check(final Path root) {
        final List<Problem> problems = new ArrayList<>();
        problems.addAll(slidesOnlyLessons(root));
        for (final Path tape : tapes(root.resolve("lessons"))) {
            problems.addAll(checkLesson(root, tape));
        }
        return problems;
    }

    private static List<Problem> checkLesson(final Path root, final Path tape) {
        final List<Problem> problems = new ArrayList<>(TapeLineChecks.check(root, tape));
        problems.addAll(narrationProblems(tape));
        problems.addAll(LessonChecks.shows(root, tape));
        problems.addAll(LiveOutputs.problems(new Media(root, lessonId(tape))));
        problems.addAll(LiveWaits.problems(new Media(root, lessonId(tape))));
        problems.addAll(DontSay.problems(new Media(root, lessonId(tape))));
        problems.addAll(ZoomChecks.problems(root, tape));
        problems.addAll(VideoLength.problems(new Media(root, lessonId(tape))));
        problems.addAll(ReadmeCommands.problems(new Media(root, lessonId(tape))));
        final Path slides = tape.resolveSibling(String.valueOf(tape.getFileName()).replace(".tape", ".slides.md"));
        if (Files.isRegularFile(slides)) {
            problems.addAll(SlideFileChecks.slides(slides));
        }
        return problems;
    }

    private static List<Problem> narrationProblems(final Path tape) {
        final Path narration = tape.resolveSibling(String.valueOf(tape.getFileName()).replace(".tape",
                ".narration.md"));
        if (!Files.isRegularFile(narration)) {
            return List.of();
        }
        final List<Problem> problems = new ArrayList<>(Narration.checkNarration(narration));
        Narration.checkSceneCount(tape, narration).ifPresent(problems::add);
        problems.addAll(LessonChecks.sceneTimes(tape, narration));
        return problems;
    }

    /* Lessons made of slides alone (no video script) still have their slides checked. */
    private static List<Problem> slidesOnlyLessons(final Path root) {
        final List<Problem> problems = new ArrayList<>();
        if (!Files.isDirectory(root.resolve("lessons"))) {
            return problems;
        }
        try (Stream<Path> folders = Files.list(root.resolve("lessons"))) {
            for (final Path folder : folders.toList()) {
                slidesWithoutATape(folder).ifPresent(slides -> problems.addAll(SlideFileChecks.slides(slides)));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return problems;
    }

    private static Optional<Path> slidesWithoutATape(final Path folder) {
        final String id = String.valueOf(folder.getFileName());
        final Path slides = folder.resolve("media").resolve(id + ".slides.md");
        final boolean alone = Files.isRegularFile(slides) && !Files.isRegularFile(slides.resolveSibling(id + ".tape"));
        return alone ? Optional.of(slides) : Optional.empty();
    }

    private static String lessonId(final Path tape) {
        return String.valueOf(tape.getFileName()).replace(".tape", "");
    }

    private static List<Path> tapes(final Path dir) {
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            return paths.filter(path -> path.toString().endsWith(".tape"))
                    .filter(path -> !insideBuildFolder(dir.relativize(path)))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /* True for generated files: anything under a folder named build, inside the folder searched. */
    private static boolean insideBuildFolder(final Path relative) {
        for (final Path part : relative) {
            if ("build".equals(part.toString())) {
                return true;
            }
        }
        return false;
    }
}
