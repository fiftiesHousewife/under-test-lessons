package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/*
 * What each live command in a lesson's scene file printed, kept in {@code <id>.outputs/01.txt}, {@code 02.txt} and so
 * on (one per live command, in scene file order; the first line is the command), so decks can show it.
 * tools/capture-lesson-outputs.py runs the commands and writes the files, with a cast of each for the deck to play. The
 * checks here fail a live command without its output, and a scene that names a line (in its narration, a callout or a
 * live command's output) without showing that line of code marked ({@code show ... --mark N}), and kept output that
 * shows a personal path: a home folder, with a person's name or account, which must never be shown.
 * {@code ANSWER_FILE} keeps the output of the lesson's exercise answer, which no scene runs. {@code SCREEN_ROWS} and
 * {@code SCREEN_COLUMNS} are one screen of terminal, which a live run must fit: the casts are at most 36 rows high.
 */
final class LiveOutputs {

    static final String FOLDER = ".outputs";
    static final String ANSWER_FILE = "answer.txt";
    static final int SCREEN_ROWS = 36;
    static final int SCREEN_COLUMNS = 150;
    private static final Pattern PERSONAL_PATH = Pattern.compile(
            "/Users/[^/\\s]+|/home/[^/\\s]+|[A-Z]:\\\\Users\\\\");

    private LiveOutputs() {
    }

    static String fileName(final int index) {
        return String.format(Locale.ROOT, "%02d.txt", index + 1);
    }

    /* Every live command in the scene file, in order. */
    static List<String> commands(final List<SceneContent> scenes) {
        return scenes.stream().flatMap(scene -> scene.commands().stream()).toList();
    }

    /* The output kept for the live command at this place in the scene file, or none if missing or out of date. */
    static List<String> read(final Media media, final int index, final String command) {
        return kept(media.file(FOLDER).resolve(fileName(index)), command);
    }

    /* What the lesson's exercise answer command printed, kept as {@code answer.txt}; empty if not kept for it. */
    static List<String> readAnswer(final Media media, final String command) {
        return kept(media.file(FOLDER).resolve(ANSWER_FILE), command);
    }

    private static List<String> kept(final Path file, final String command) {
        if (!Files.isRegularFile(file)) {
            return List.of();
        }
        final List<String> lines = lines(file);
        return !lines.isEmpty() && lines.getFirst().equals("$ " + command) ? lines.subList(1, lines.size()) : List.of();
    }

    /* Problems in a lesson: live commands without kept output, and lines the narration names that aren't shown. */
    static List<Problem> problems(final Media media) {
        final Path sceneFile = media.scenes();
        if (!Files.isRegularFile(sceneFile)) {
            return List.of();
        }
        final List<SceneContent> scenes;
        try {
            scenes = SceneContent.read(media.root(), sceneFile);
        } catch (IllegalArgumentException e) {
            return List.of(); // a show line that finds no file, which LessonChecks.shows reports
        }
        final List<Problem> problems = new ArrayList<>(keptOutputs(media, commands(scenes)));
        final Path narration = media.file(".narration.md");
        final List<String> paragraphs = Files.isRegularFile(narration) ? Narration.paragraphs(narration) : List.of();
        for (int i = 0; i < scenes.size(); i++) {
            final String said = paragraph(paragraphs, i) + " " + shownText(scenes.get(i));
            problems.addAll(MarkedLines.unshown(sceneFile, new MarkedLines.Said(i + 1, said), scenes.get(i)));
        }
        return problems;
    }

    private static String paragraph(final List<String> paragraphs, final int index) {
        return index < paragraphs.size() ? paragraphs.get(index) : "";
    }

    /* What a scene's callouts and live commands' output say. */
    private static String shownText(final SceneContent scene) {
        final List<String> printed = scene.outputs().stream().flatMap(List::stream).toList();
        return String.join(" ", scene.callouts()) + " " + String.join(" ", printed);
    }

    /*
     * The rows a kept output takes on a screen of terminal: its command's line, then each line, a long one wrapping
     * onto more rows.
     */
    static int rowsOnScreen(final List<String> output) {
        return 1 + output.stream().mapToInt(line -> Math.max(1, (line.length() + SCREEN_COLUMNS - 1) / SCREEN_COLUMNS))
                .sum();
    }

    /* Each live command has its output kept, fitting one screen, and no kept output shows a personal path. */
    private static List<Problem> keptOutputs(final Media media, final List<String> commands) {
        final List<Problem> problems = new ArrayList<>();
        for (int i = 0; i < commands.size(); i++) {
            problems.addAll(keptOutput(media, i, commands.get(i)));
        }
        return problems;
    }

    private static List<Problem> keptOutput(final Media media, final int index, final String command) {
        final Path sceneFile = media.scenes();
        final List<String> output = read(media, index, command);
        final List<Problem> problems = new ArrayList<>();
        if (output.stream().anyMatch(line -> PERSONAL_PATH.matcher(line).find())) {
            problems.add(new Problem(sceneFile, "live command " + (index + 1) + "'s kept output shows a personal path;"
                    + " capture it again with tools/capture-lesson-outputs.py"));
        }
        if (output.isEmpty()) {
            problems.add(new Problem(sceneFile, "live command " + (index + 1) + " (" + command + ") has no kept"
                    + " output for decks; run python3 tools/capture-lesson-outputs.py " + media.safeLesson()));
        }
        final int rows = rowsOnScreen(output);
        if (rows > SCREEN_ROWS) {
            problems.add(new Problem(sceneFile, "live command " + (index + 1) + " (" + command + ") prints " + rows
                    + " rows, counting its command and lines that wrap, but one screen holds " + SCREEN_ROWS
                    + "; make it print less: fewer tests in the run, or shorter failure messages"));
        }
        return problems;
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
