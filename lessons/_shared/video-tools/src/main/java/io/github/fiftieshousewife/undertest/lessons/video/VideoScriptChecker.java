package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
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

    private static final Pattern TYPE = Pattern.compile("^Type(?:@\\S+)?\\s+([\"'`])(.*)\\1\\s*$");
    private static final Pattern WAIT = Pattern.compile("^Wait(\\+\\w+)?(?:@\\S+)?(?:\\s+/(.*)/)?\\s*$");
    private static final Pattern SOURCE = Pattern.compile("^Source\\s+(\\S+)\\s*$");
    private static final Pattern SCENE = Pattern.compile("^#\\s*-+\\s*Scene\\s+\\d+:.*-+\\s*$");

    /** Checks every {@code .tape} file under {@code lessons/}; paths in scripts are relative to {@code root}. */
    public List<Problem> check(final Path root) {
        final List<Problem> problems = new ArrayList<>();
        for (final Path tape : tapes(root.resolve("lessons"))) {
            problems.addAll(checkTape(root, tape));
            final Path narration = tape.resolveSibling(String.valueOf(tape.getFileName()).replace(".tape",
                    ".narration.md"));
            if (Files.isRegularFile(narration)) {
                problems.addAll(checkNarration(narration));
                checkSceneCount(tape, narration).ifPresent(problems::add);
                problems.addAll(LessonChecks.sceneTimes(tape, narration));
            }
            problems.addAll(LessonChecks.shows(root, tape));
            problems.addAll(LiveOutputs.problems(new Media(root, lessonId(tape))));
            problems.addAll(ZoomChecks.problems(root, tape));
            problems.addAll(VideoLength.problems(new Media(root, lessonId(tape))));
            final Path slides = tape.resolveSibling(String.valueOf(tape.getFileName()).replace(".tape", ".slides.md"));
            if (Files.isRegularFile(slides)) {
                problems.addAll(LessonChecks.slides(slides));
            }
        }
        return problems;
    }

    private static String lessonId(final Path tape) {
        return String.valueOf(tape.getFileName()).replace(".tape", "");
    }

    /** Captions and voice pair each scene with one narration item, so the counts must match. */
    static Optional<Problem> checkSceneCount(final Path tape, final Path narration) {
        final long scenes = readLines(tape).stream().filter(line -> SCENE.matcher(line).matches()).count();
        final int items = Narration.rawParagraphs(narration).size();
        return scenes == items ? Optional.empty() : Optional.of(new Problem(tape, scenes + " scenes but " + items
                + " narration items; each scene needs exactly one item (look for a repeated scene line)"));
    }

    /** Narration is spoken, so it must never contain a command: say what happens instead. */
    static List<Problem> checkNarration(final Path narration) {
        final List<String> paragraphs = Narration.rawParagraphs(narration);
        final List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < paragraphs.size(); i++) {
            final String text = paragraphs.get(i);
            if (text.contains("`") || text.contains("./") || text.contains("gradlew")) {
                problems.add(new Problem(narration, "item " + (i + 1) + " contains a command, which the voice would"
                        + " read aloud; say what happens instead (for example, \"Now we run the strong tests\")"));
            }
        }
        return problems;
    }

    List<Problem> checkTape(final Path root, final Path tape) {
        final List<String> lines = readLines(tape);
        final StringBuilder typed = new StringBuilder();
        final List<Problem> problems = new ArrayList<>();
        boolean commandRunning = false;
        for (int i = 0; i < lines.size(); i++) {
            final Line line = new Line(tape, i + 1, lines.get(i).strip());
            problems.addAll(checkLine(root, line, typed));
            if (commandRunning && line.text().startsWith("Type")) {
                problems.add(line.problem("the previous command may still be running, so this typing would mix into"
                        + " its output; add Sleep 300ms (or a Wait) after its Enter"));
            }
            commandRunning = nextCommandRunning(commandRunning, line.text());
            typedText(line.text()).ifPresent(text -> typed.append(text).append('\n'));
        }
        return problems;
    }

    /** After Type then Enter, a command runs until a Sleep or Wait gives it time to finish. */
    private static boolean nextCommandRunning(final boolean running, final String line) {
        if (line.startsWith("Sleep") || line.startsWith("Wait")) {
            return false;
        }
        return running || "Enter".equals(line);
    }

    private static List<Problem> checkLine(final Path root, final Line line, final CharSequence typed) {
        final List<Problem> problems = new ArrayList<>();
        final Matcher wait = WAIT.matcher(line.text());
        if (wait.matches()) {
            problems.addAll(checkWait(line, wait, typed));
        }
        typedText(line.text()).flatMap(text -> checkTyped(line, text)).ifPresent(problems::add);
        final Matcher source = SOURCE.matcher(line.text());
        if (source.matches() && !Files.isRegularFile(root.resolve(source.group(1)))) {
            problems.add(line.problem("Source file not found: " + source.group(1)));
        }
        return problems;
    }

    private static List<Problem> checkWait(final Line line, final Matcher wait, final CharSequence typed) {
        final List<Problem> problems = new ArrayList<>();
        if (!"+Screen".equals(wait.group(1))) {
            problems.add(line.problem("Wait looks only at the last line, which is usually the next prompt by the time"
                    + " the output appears; use Wait+Screen"));
        }
        if (wait.group(2) == null) {
            problems.add(line.problem("Wait has no pattern, so it waits for VHS's default prompt; give one, for"
                    + " example Wait+Screen /DONE/"));
            return problems;
        }
        try {
            if (Pattern.compile(wait.group(2)).matcher(typed).find()) {
                problems.add(line.problem("Wait pattern already matches text typed earlier, so it stops waiting at"
                        + " once; make the expected output differ from the command (see lessons/_shared/setup.tape)"));
            }
        } catch (PatternSyntaxException e) {
            problems.add(line.problem("Wait pattern is not a valid regular expression: " + e.getDescription()));
        }
        return problems;
    }

    private static Optional<Problem> checkTyped(final Line line, final String text) {
        if (line.text().matches("^Type(?:@\\S+)?\\s+\".*") && text.contains("\\\"")) {
            return Optional.of(line.problem("VHS strings have no escape characters, so \\\" ends the string early;"
                    + " put the text in backticks instead"));
        }
        if (text.contains("./gradlew") && text.contains("| grep") && !text.contains("--rerun")) {
            return Optional.of(line.problem("Gradle output piped into grep needs --rerun: a task skipped as up to"
                    + " date prints nothing, so the recording waits for output that never comes"));
        }
        return Optional.empty();
    }

    private static Optional<String> typedText(final String line) {
        final Matcher type = TYPE.matcher(line);
        return type.matches() ? Optional.of(type.group(2)) : Optional.empty();
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

    private static List<String> readLines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** One line of a script, so problems can say where they are. */
    record Line(Path tape, int number, String text) {
        Problem problem(final String message) {
            return new Problem(tape, "line " + number + ": " + message);
        }
    }

    /** True for generated files: anything under a folder named build, inside the folder searched. */
    private static boolean insideBuildFolder(final Path relative) {
        for (final Path part : relative) {
            if ("build".equals(part.toString())) {
                return true;
            }
        }
        return false;
    }
}
