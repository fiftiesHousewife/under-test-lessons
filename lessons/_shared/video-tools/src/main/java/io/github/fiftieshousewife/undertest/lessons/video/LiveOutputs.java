package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * What each live command in a lesson's video script printed, kept in {@code <id>.outputs/01.txt}, {@code 02.txt} and
 * so on (one per live command, in script order; the first line is the command), so decks can show the output the
 * video shows. tools/capture-lesson-outputs.py runs the commands as a recording does and writes the files. The checks
 * here fail a live command without its output, and a scene that names a line (in its narration, a callout or a live
 * command's output) without showing that line of code marked ({@code show ... --mark N}).
 */
final class LiveOutputs {

    static final String FOLDER = ".outputs";
    /** The kept output of the lesson's exercise answer, which no video runs. */
    static final String ANSWER_FILE = "answer.txt";
    /** What the video's terminal holds: 1920 by 1080 at font size 20, less its padding and window bar. */
    static final int SCREEN_ROWS = 36;
    static final int SCREEN_COLUMNS = 150;
    /** A home folder in a path: a person's name or account, which must never be shown. */
    private static final Pattern PERSONAL_PATH = Pattern.compile("/Users/[^/\\s]+|/home/[^/\\s]+|[A-Z]:\\\\Users\\\\");
    private static final Pattern LINE_NAMED = Pattern.compile("(?i)\\blines?\\s+(\\d+)\\b");
    private static final Pattern NUMBERED = Pattern.compile("\\s*(\\d+)\\s");

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

    /** What the lesson's exercise answer command printed, kept as {@code answer.txt}; empty if not kept for it. */
    static List<String> readAnswer(final Media media, final String command) {
        final Path file = media.file(FOLDER).resolve(ANSWER_FILE);
        if (!Files.isRegularFile(file)) {
            return List.of();
        }
        final List<String> lines = lines(file);
        return !lines.isEmpty() && lines.getFirst().equals("$ " + command) ? lines.subList(1, lines.size()) : List.of();
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
        final List<Problem> problems = new ArrayList<>(keptOutputs(media, commands(scenes)));
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

    /**
     * The rows a kept output takes in the video's terminal: its command's line, then each line, a long one wrapping
     * onto more rows.
     */
    static int rowsOnScreen(final List<String> output) {
        return 1 + output.stream().mapToInt(line -> Math.max(1, (line.length() + SCREEN_COLUMNS - 1) / SCREEN_COLUMNS))
                .sum();
    }

    /** Each live command has its output kept, fitting one screen, and no kept output shows a personal path. */
    private static List<Problem> keptOutputs(final Media media, final List<String> commands) {
        final Path tape = media.file(".tape");
        final List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < commands.size(); i++) {
            final List<String> output = read(media, i, commands.get(i));
            if (output.stream().anyMatch(line -> PERSONAL_PATH.matcher(line).find())) {
                problems.add(new Problem(tape, "live command " + (i + 1) + "'s kept output shows a personal path;"
                        + " capture it again with tools/capture-lesson-outputs.py"));
            }
            if (output.isEmpty()) {
                problems.add(new Problem(tape, "live command " + (i + 1) + " (" + commands.get(i) + ") has no kept"
                        + " output for decks; run python3 tools/capture-lesson-outputs.py " + media.safeLesson()));
            }
            final int rows = rowsOnScreen(output);
            if (rows > SCREEN_ROWS) {
                problems.add(new Problem(tape, "live command " + (i + 1) + " (" + commands.get(i) + ") prints " + rows
                        + " rows, counting its command and lines that wrap, but one screen holds " + SCREEN_ROWS
                        + "; make it print less: fewer tests in the run, or shorter failure messages"));
            }
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

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
