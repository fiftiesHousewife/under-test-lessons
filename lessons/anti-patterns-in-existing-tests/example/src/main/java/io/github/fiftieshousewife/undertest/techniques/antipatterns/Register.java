package io.github.fiftieshousewife.undertest.techniques.antipatterns;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The register of anti-patterns: every one found in the old tests, written down with the decision taken on it. Each
 * line is a kind, a test and a decision, separated by two or more spaces; lines starting with # are comments. A
 * finding the register doesn't hold is new, and a row no finding matches has been fixed.
 */
public final class Register {

    private static final String NOT_RECORDED = "not in the register";

    private final Map<String, String> decisions;

    private Register(final Map<String, String> decisions) {
        this.decisions = decisions;
    }

    /** A register with nothing written down yet. */
    public static Register empty() {
        return new Register(Map.of());
    }

    /** Reads a register from its lines. */
    public static Register of(final List<String> lines) {
        final Map<String, String> decisions = new LinkedHashMap<>();
        lines.stream().map(String::strip).filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .map(line -> line.split(" {2,}", 3))
                .forEach(row -> decisions.put(key(row[0], row[1]), row.length > 2 ? row[2] : ""));
        return new Register(decisions);
    }

    /** The findings the register doesn't hold: new anti-patterns, which the ratchet refuses. */
    public List<Finding> unrecorded(final List<Finding> findings) {
        return findings.stream().filter(finding -> !decisions.containsKey(key(finding.kind(), finding.test())))
                .toList();
    }

    /** The register's rows that no finding matches any more: anti-patterns fixed, to take out of the register. */
    public List<String> fixed(final List<Finding> findings) {
        final List<String> rows = new ArrayList<>(decisions.keySet());
        findings.forEach(finding -> rows.remove(key(finding.kind(), finding.test())));
        return rows;
    }

    /** The findings as a table: each one's kind, test, file and line, and its decision, or that it has none yet. */
    public List<String> report(final List<Finding> findings) {
        final List<String> lines = new ArrayList<>();
        lines.add(String.format("  %-18s %-48s %-24s %s", "kind", "test", "where", "decision"));
        findings.forEach(finding -> lines.add(String.format("  %-18s %-48s %-24s %s", finding.kind(), finding.test(),
                finding.where(), decisions.getOrDefault(key(finding.kind(), finding.test()), NOT_RECORDED))));
        return lines;
    }

    private static String key(final String kind, final String test) {
        return kind + " " + test;
    }
}
