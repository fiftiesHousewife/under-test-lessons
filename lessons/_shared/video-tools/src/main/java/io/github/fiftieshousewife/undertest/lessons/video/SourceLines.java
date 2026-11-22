package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Picks lines of a source file the way the {@code show} helper does, each prefixed with its line number. */
final class SourceLines {

    static final String GAP = "   ⋮";

    private SourceLines() {
    }

    static String numbered(final int number, final String line) {
        return "%3d  %s".formatted(number, line);
    }

    /** Lines {@code from} to {@code to}, counting from 1. */
    static List<String> range(final List<String> lines, final int from, final int to) {
        final List<String> out = new ArrayList<>();
        for (int i = Math.max(1, from); i <= Math.min(to, lines.size()); i++) {
            out.add(numbered(i, lines.get(i - 1)));
        }
        return out;
    }

    /** In a TOML file: each matching line under its [section] header, with a gap marker between runs. */
    static List<String> tomlMatches(final List<String> lines, final String pattern) {
        final Pattern match = Pattern.compile(pattern);
        final List<String> out = new ArrayList<>();
        int section = 0;
        int shownSection = 0;
        int last = 0;
        for (int i = 1; i <= lines.size(); i++) {
            final String line = lines.get(i - 1);
            if (line.startsWith("[")) {
                section = i;
            } else if (match.matcher(line).find()) {
                if (section != shownSection) {
                    addGapIf(out, !out.isEmpty());
                    out.add(numbered(section, lines.get(section - 1)));
                    shownSection = section;
                    last = section;
                }
                addGapIf(out, i > last + 1);
                out.add(numbered(i, line));
                last = i;
            }
        }
        return out;
    }

    private static void addGapIf(final List<String> out, final boolean gap) {
        if (gap) {
            out.add(GAP);
        }
    }

    /** The whole { ... } block the first matching line opens, or else the nearest block around it. */
    static List<String> blockAround(final List<String> lines, final String pattern) {
        final Pattern match = Pattern.compile(pattern);
        int found = -1;
        for (int i = 0; i < lines.size() && found < 0; i++) {
            if (match.matcher(lines.get(i)).find()) {
                found = i;
            }
        }
        if (found < 0) {
            return List.of();
        }
        int start = found;
        while (start > 0 && depthChange(lines.get(start)) <= 0) {
            start--;
        }
        int depth = 0;
        int end = start;
        do {
            depth += depthChange(lines.get(end));
            end++;
        } while (end < lines.size() && depth > 0);
        return range(lines, start + 1, end);
    }

    private static int depthChange(final String line) {
        return (int) (line.chars().filter(c -> c == '{').count() - line.chars().filter(c -> c == '}').count());
    }
}
