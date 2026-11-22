package io.github.fiftieshousewife.undertest.lessons.video;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * What each live command in a lesson's video script printed, kept in {@code <id>.outputs/01.txt}, {@code 02.txt} and
 * so on (one per live command, in script order; the first line is the command), so decks can show the output the
 * video shows. {@code captureLessonOutputs} runs the commands as a recording does and writes the files. The checks
 * here fail a live command without its output, and a scene that names a line (in its narration, a callout or a live
 * command's output) without showing that line of code marked ({@code show ... --mark N}).
 */
@Slf4j
final class LiveOutputs {

    static final String FOLDER = ".outputs";
    /** Lines a sandbox or the JVM adds, which a viewer never sees in the recording. */
    private static final List<String> NOISE = List.of("Picked up JAVA_TOOL_OPTIONS", "Caught exception: Could not");
    private static final Pattern ESCAPE = Pattern.compile("\u001B\\[[0-9;?]*[A-Za-z]");
    private static final Pattern LINE_NAMED = Pattern.compile("(?i)\\blines?\\s+(\\d+)\\b");
    private static final Pattern NUMBERED = Pattern.compile("\\s*(\\d+)\\s");
    /** Settings a recording makes off camera (lessons/_shared/setup.tape), so the output matches the video's. */
    private static final String QUIET = " -Dorg.gradle.warning.mode=none -Dorg.gradle.configuration-cache=false"
            + " -Dorg.gradle.logging.level=quiet";

    private LiveOutputs() {
    }

    static String fileName(final int index) {
        return String.format(Locale.ROOT, "%02d.txt", index + 1);
    }

    /** Every live command in the script, in order. */
    static List<String> commands(final List<SceneContent> scenes) {
        return scenes.stream().flatMap(scene -> scene.commands().stream()).toList();
    }

    /** The output kept for the live command at this place in the script, or none if it is missing or out of date. */
    static List<String> read(final Media media, final int index, final String command) {
        final Path file = media.file(FOLDER).resolve(fileName(index));
        if (!Files.isRegularFile(file)) {
            return List.of();
        }
        final List<String> lines = lines(file);
        return !lines.isEmpty() && lines.getFirst().equals("$ " + command) ? lines.subList(1, lines.size()) : List.of();
    }

    /** Runs each live command from the repository root, as a recording would, and keeps what it printed. */
    static int capture(final Media media) {
        final List<String> commands = commands(SceneContent.read(media.root(), media.file(".tape")));
        final Path folder = media.file(FOLDER);
        createFolder(folder);
        for (int i = 0; i < commands.size(); i++) {
            final String output = clean(run(media.root(), commands.get(i)));
            write(folder.resolve(fileName(i)), "$ " + commands.get(i) + "\n" + output + "\n");
        }
        log.info("{}: output of {} live commands kept", media.safeLesson(), commands.size());
        return commands.size();
    }

    /** The output as a viewer sees it: no colour codes, no sandbox or JVM notices, no blank lines at either end. */
    static String clean(final String output) {
        final List<String> kept = ESCAPE.matcher(output).replaceAll("").lines()
                .filter(line -> NOISE.stream().noneMatch(line::startsWith)).map(String::stripTrailing).toList();
        return String.join("\n", kept).strip();
    }

    /** Problems in a lesson: live commands without kept output, and lines the narration names that aren't shown. */
    static List<Problem> problems(final Media media) {
        final Path tape = media.file(".tape");
        if (!Files.isRegularFile(tape)) {
            return List.of();
        }
        final List<SceneContent> scenes;
        try {
            scenes = SceneContent.read(media.root(), tape);
        } catch (IllegalArgumentException e) {
            return List.of(); // a show line that finds no file, which LessonChecks.shows reports
        }
        final List<Problem> problems = new ArrayList<>();
        final List<String> commands = commands(scenes);
        for (int i = 0; i < commands.size(); i++) {
            if (read(media, i, commands.get(i)).isEmpty()) {
                problems.add(new Problem(tape, "live command " + (i + 1) + " (" + commands.get(i) + ") has no kept"
                        + " output for decks; run ./gradlew :lessons:video-tools:captureLessonOutputs -Plesson="
                        + media.safeLesson()));
            }
        }
        final Path narration = media.file(".narration.md");
        final List<String> paragraphs = Files.isRegularFile(narration) ? Narration.paragraphs(narration) : List.of();
        for (int i = 0; i < scenes.size(); i++) {
            final List<String> printed = scenes.get(i).outputs().stream().flatMap(List::stream).toList();
            final String said = (i < paragraphs.size() ? paragraphs.get(i) : "") + " "
                    + String.join(" ", scenes.get(i).callouts()) + " " + String.join(" ", printed);
            problems.addAll(unshownLines(tape, new Said(i + 1, said), scenes.get(i)));
        }
        return problems;
    }

    /** What a scene's narration and callouts say, and the scene's number. */
    private record Said(int scene, String text) {
    }

    private static List<Problem> unshownLines(final Path tape, final Said said, final SceneContent content) {
        final List<Problem> problems = new ArrayList<>();
        final Set<Integer> lines = new TreeSet<>();
        final Matcher named = LINE_NAMED.matcher(said.text());
        while (named.find()) {
            lines.add(Integer.parseInt(named.group(1)));
        }
        for (final int line : lines) {
            if (!shows(content, line)) {
                problems.add(new Problem(tape, "scene " + said.scene() + " names line " + line + " (in its"
                        + " narration, a callout or a live command's output), but doesn't show it marked; show the"
                        + " code in the scene with --mark " + line));
            }
        }
        return problems;
    }

    /** True if a code panel in the scene shows the line, marked ({@code show ... --mark N}). */
    static boolean shows(final SceneContent content, final int line) {
        return content.panels().stream().anyMatch(panel -> panel.marked().contains(line)
                && panel.lines().stream().anyMatch(shown -> lineNumber(shown) == line));
    }

    /** Every line marked in the scene's code panels. */
    static Set<Integer> marked(final SceneContent content) {
        final Set<Integer> marked = new TreeSet<>();
        content.panels().forEach(panel -> marked.addAll(panel.marked()));
        return marked;
    }

    /** The number at the start of a panel line ({@code " 24  code"}), or -1 for a gap or a line without one. */
    static int lineNumber(final String panelLine) {
        final Matcher number = NUMBERED.matcher(panelLine);
        return number.lookingAt() ? Integer.parseInt(number.group(1)) : -1;
    }

    /** True if a line of output names one of these line numbers ("line 24"). */
    static boolean namesAny(final String outputLine, final Set<Integer> lines) {
        final Matcher named = LINE_NAMED.matcher(outputLine);
        while (named.find()) {
            if (lines.contains(Integer.parseInt(named.group(1)))) {
                return true;
            }
        }
        return false;
    }

    @SuppressFBWarnings(value = "COMMAND_INJECTION",
            justification = "Runs the commands of this repository's own video scripts, as their recordings do")
    private static String run(final Path root, final String command) {
        try {
            final ProcessBuilder builder = new ProcessBuilder("bash", "-c", command).directory(root.toFile())
                    .redirectErrorStream(true).redirectInput(ProcessBuilder.Redirect.from(nullDevice()));
            builder.environment().merge("GRADLE_OPTS", QUIET, String::concat);
            final Process process = builder.start();
            final String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            process.waitFor();
            return output;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while running " + command, e);
        }
    }

    private static java.io.File nullDevice() {
        return new java.io.File(System.getProperty("os.name", "").startsWith("Windows") ? "NUL" : "/dev/null");
    }

    private static void write(final Path file, final String text) {
        try {
            Files.writeString(file, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void createFolder(final Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
