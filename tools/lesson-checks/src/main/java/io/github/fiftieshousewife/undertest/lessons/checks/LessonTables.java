package io.github.fiftieshousewife.undertest.lessons.checks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/*
 * The two hand-kept tables that repeat what lesson.yaml files say: the catalogue's table of every toolkit technique and
 * its lessons, and the lessons index's table of every lesson and its techniques.
 */
final class LessonTables {

    static final String CATALOGUE_PAGE = "lessons/CATALOGUE.md";
    static final String CATALOGUE_HEADING = "## Every toolkit technique, and its lesson";
    static final String INDEX_PAGE = "lessons/README.md";
    static final String INDEX_HEADING = "## All lessons";

    private static final Pattern LESSON_LINK = Pattern.compile("\\]\\(([a-z0-9-]+)/README\\.md\\)");
    private static final Pattern DIVIDER = Pattern.compile("^\\|[-|\\s]+\\|$");

    private LessonTables() {
    }

    /* Each technique row links exactly the lessons that name it; with the toolkit, every technique has a row. */
    static List<Problem> catalogue(final Path root, final List<Lesson> lessons, final Optional<Set<String>> toolkit) {
        final Path file = root.resolve(CATALOGUE_PAGE);
        final Map<String, Set<String>> taught = lessonsByTechnique(lessons);
        final Map<String, Set<String>> rows = new TreeMap<>();
        for (final List<String> row : rows(file, CATALOGUE_HEADING)) {
            rows.put(row.getFirst(), Text.matches(LESSON_LINK, row.get(1)));
        }
        final List<Problem> problems = new ArrayList<>();
        rows.forEach((technique, linked) -> {
            final Set<String> expected = taught.getOrDefault(technique, Set.of());
            if (!linked.equals(expected)) {
                problems.add(new Problem(file, technique + ": the row links " + linked
                        + ", but the lessons that name it in lesson.yaml are " + expected));
            }
        });
        final Set<String> needed = new TreeSet<>(taught.keySet());
        toolkit.ifPresent(needed::addAll);
        needed.stream().filter(technique -> !rows.containsKey(technique))
                .forEach(technique -> problems.add(new Problem(file, "no row for the technique " + technique)));
        toolkit.ifPresent(ids -> rows.keySet().stream().filter(technique -> !ids.contains(technique))
                .forEach(technique -> problems.add(new Problem(file, "a row for " + technique
                        + ", which the toolkit doesn't have"))));
        return problems;
    }

    /* Each lesson has a row, and the row links exactly the techniques its lesson.yaml names. */
    static List<Problem> index(final Path root, final List<Lesson> lessons) {
        final Path file = root.resolve(INDEX_PAGE);
        final Map<String, Set<String>> rows = new TreeMap<>();
        for (final List<String> row : rows(file, INDEX_HEADING)) {
            Text.matches(LESSON_LINK, row.getFirst())
                    .forEach(id -> rows.put(id, Text.matches(TechniqueLinks.TECHNIQUE_LINK, row.get(1))));
        }
        final List<Problem> problems = new ArrayList<>();
        for (final Lesson lesson : lessons) {
            final Set<String> linked = rows.get(lesson.id());
            if (linked == null) {
                problems.add(new Problem(file, "no row for the lesson " + lesson.id()));
            } else if (!linked.equals(new TreeSet<>(lesson.techniques()))) {
                problems.add(new Problem(file, lesson.id() + ": the row links " + linked
                        + ", but its lesson.yaml names " + new TreeSet<>(lesson.techniques())));
            }
        }
        return problems;
    }

    /* The cells of each row of the first table under a heading, without its header and divider rows. */
    static List<List<String>> rows(final Path file, final String heading) {
        final List<String> lines = Text.read(file).lines().toList();
        final int start = lines.indexOf(heading);
        if (start < 0) {
            throw new IllegalArgumentException(file + " has no heading " + heading);
        }
        final List<List<String>> rows = new ArrayList<>();
        for (final String line : lines.subList(start + 1, lines.size())) {
            if (line.startsWith("## ")) {
                break;
            }
            if (line.startsWith("|") && !DIVIDER.matcher(line).matches()) {
                rows.add(cells(line));
            }
        }
        return rows.isEmpty() ? rows : rows.subList(1, rows.size());
    }

    private static List<String> cells(final String line) {
        final String inner = line.strip().substring(1, line.strip().length() - 1);
        return Arrays.stream(inner.split("\\|", -1)).map(String::strip).toList();
    }

    private static Map<String, Set<String>> lessonsByTechnique(final List<Lesson> lessons) {
        final Map<String, Set<String>> taught = new TreeMap<>();
        for (final Lesson lesson : lessons) {
            lesson.techniques().forEach(technique -> taught.computeIfAbsent(technique, key -> new TreeSet<>())
                    .add(lesson.id()));
        }
        return taught;
    }
}
