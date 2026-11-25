package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * The code lines a scene names ("line 24", in its narration, a callout or a live command's output) and the lines its
 * code panels show marked ({@code show ... --mark N}).
 */
final class MarkedLines {

    private static final Pattern LINE_NAMED = Pattern.compile("(?i)\\blines?\\s+(\\d+)\\b");
    private static final Pattern MARK_PART = Pattern.compile("(\\d+)(?:-(\\d+))?");
    private static final Pattern NUMBERED = Pattern.compile("\\s*(\\d+)\\s");

    private MarkedLines() {
    }

    /* What a scene's narration and callouts say, and the scene's number. */
    record Said(int scene, String text) {
    }

    static List<Problem> unshown(final Path tape, final Said said, final SceneContent content) {
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

    /* True if a code panel in the scene shows the line, marked ({@code show ... --mark N}). */
    static boolean shows(final SceneContent content, final int line) {
        return content.panels().stream().anyMatch(panel -> panel.marked().contains(line)
                && panel.lines().stream().anyMatch(shown -> lineNumber(shown) == line));
    }

    /* Every line marked in the scene's code panels. */
    static Set<Integer> marked(final SceneContent content) {
        final Set<Integer> marked = new TreeSet<>();
        content.panels().forEach(panel -> marked.addAll(panel.marked()));
        return marked;
    }

    /* The number at the start of a panel line ({@code " 24  code"}), or -1 for a gap or a line without one. */
    static int lineNumber(final String panelLine) {
        final Matcher number = NUMBERED.matcher(panelLine);
        return number.lookingAt() ? Integer.parseInt(number.group(1)) : -1;
    }

    /* True if a line of output names one of these line numbers ("line 24"). */
    static boolean namesAny(final String outputLine, final Set<Integer> lines) {
        final Matcher named = LINE_NAMED.matcher(outputLine);
        while (named.find()) {
            if (lines.contains(Integer.parseInt(named.group(1)))) {
                return true;
            }
        }
        return false;
    }

    /* The lines a {@code show} line zooms in on ({@code --zoom 22-24}), or none. */
    static Set<Integer> zoomed(final List<String> words) {
        final int zoom = words.indexOf("--zoom");
        return zoom >= 0 && zoom + 1 < words.size() ? marks(words.get(zoom + 1)) : Set.of();
    }

    /* The line numbers in a list such as {@code 18,22-24}. */
    static Set<Integer> marks(final String list) {
        final Set<Integer> numbers = new TreeSet<>();
        for (final String part : list.split(",")) {
            addRange(numbers, part);
        }
        return numbers;
    }

    private static void addRange(final Set<Integer> numbers, final String part) {
        final Matcher range = MARK_PART.matcher(part.strip());
        if (!range.matches()) {
            throw new IllegalArgumentException("show --mark: not a line number or range: " + part);
        }
        final int from = Integer.parseInt(range.group(1));
        final int to = range.group(2) == null ? from : Integer.parseInt(range.group(2));
        for (int n = from; n <= to; n++) {
            numbers.add(n);
        }
    }
}
