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

/*
 * The line-by-line checks of one video script ({@link VideoScriptChecker} lists them): its {@code Wait} lines, what it
 * types, its {@code Source} files, and typing while a command may still be running.
 */
final class TapeLineChecks {

    private static final Pattern TYPE = Pattern.compile("^Type(?:@\\S+)?\\s+([\"'`])(.*)\\1\\s*$");
    private static final Pattern WAIT = Pattern.compile("^Wait(\\+\\w+)?(?:@\\S+)?(?:\\s+/(.*)/)?\\s*$");
    private static final Pattern SOURCE = Pattern.compile("^Source\\s+(\\S+)\\s*$");

    private TapeLineChecks() {
    }

    static List<Problem> check(final Path root, final Path tape) {
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

    /* After Type then Enter, a command runs until a Sleep or Wait gives it time to finish. */
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
        matchesTyped(line, wait.group(2), typed).ifPresent(problems::add);
        return problems;
    }

    private static Optional<Problem> matchesTyped(final Line line, final String pattern, final CharSequence typed) {
        try {
            return Pattern.compile(pattern).matcher(typed).find()
                    ? Optional.of(line.problem("Wait pattern already matches text typed earlier, so it stops waiting"
                    + " at once; make the expected output differ from the command (see lessons/_shared/setup.tape)"))
                    : Optional.empty();
        } catch (PatternSyntaxException e) {
            return Optional.of(line.problem("Wait pattern is not a valid regular expression: " + e.getDescription()));
        }
    }

    private static Optional<Problem> checkTyped(final Line line, final String text) {
        if (escapedQuote(line, text)) {
            return Optional.of(line.problem("VHS strings have no escape characters, so \\\" ends the string early;"
                    + " put the text in backticks instead"));
        }
        if (grepWithoutRerun(text)) {
            return Optional.of(line.problem("Gradle output piped into grep needs --rerun: a task skipped as up to"
                    + " date prints nothing, so the recording waits for output that never comes"));
        }
        return Optional.empty();
    }

    private static boolean escapedQuote(final Line line, final String text) {
        return line.text().matches("^Type(?:@\\S+)?\\s+\".*") && text.contains("\\\"");
    }

    private static boolean grepWithoutRerun(final String text) {
        return text.contains("./gradlew") && text.contains("| grep") && !text.contains("--rerun");
    }

    private static Optional<String> typedText(final String line) {
        final Matcher type = TYPE.matcher(line);
        return type.matches() ? Optional.of(type.group(2)) : Optional.empty();
    }

    /* One line of a script, so problems can say where they are. */
    record Line(Path tape, int number, String text) {
        Problem problem(final String message) {
            return new Problem(tape, "line " + number + ": " + message);
        }
    }

    private static List<String> readLines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
