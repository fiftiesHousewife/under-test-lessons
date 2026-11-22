package io.github.fiftieshousewife.undertest.lessons.checks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point: {@code <repository root> [<toolkit checkout>]}. Fails if the lesson.yaml files, the catalogue, the
 * lessons index and (when a toolkit checkout is given) the toolkit's techniques disagree.
 */
@Slf4j
public final class LessonFileChecks {

    private LessonFileChecks() {
    }

    public static void main(final String... args) {
        if (args.length == 0 || args.length > 2) {
            throw new IllegalArgumentException("usage: LessonFileChecks <repository root> [<toolkit checkout>]");
        }
        final Optional<Set<String>> techniques = args.length == 2
                ? Optional.of(new Toolkit(Path.of(args[1])).techniqueIds())
                : Optional.empty();
        if (techniques.isEmpty()) {
            log.warn("No toolkit checkout found, so lessons were not checked against the toolkit's techniques."
                    + " Put under-test beside this repository, or pass -PtoolkitDir=<path>.");
        }
        failOn(check(Path.of(args[0]), techniques));
    }

    static List<Problem> check(final Path root, final Optional<Set<String>> techniques) {
        final List<Problem> problems = new ArrayList<>();
        final List<Lesson> lessons = new LessonReader().read(root, problems);
        problems.addAll(LessonTables.catalogue(root, lessons, techniques));
        problems.addAll(LessonTables.index(root, lessons));
        techniques.ifPresent(ids -> {
            problems.addAll(unknownTechniques(root, lessons, ids));
            problems.addAll(TechniqueLinks.check(root, ids));
        });
        return problems;
    }

    private static List<Problem> unknownTechniques(final Path root, final List<Lesson> lessons, final Set<String> ids) {
        final List<Problem> problems = new ArrayList<>();
        for (final Lesson lesson : lessons) {
            final Path file = root.resolve("lessons/" + lesson.id() + "/lesson.yaml");
            lesson.techniques().stream().filter(technique -> !ids.contains(technique))
                    .forEach(technique -> problems.add(new Problem(file,
                            "names the technique " + technique + ", which the toolkit doesn't have")));
        }
        return problems;
    }

    private static void failOn(final List<Problem> problems) {
        problems.forEach(problem -> log.error("{}", problem.toString().replace('\r', ' ').replace('\n', ' ')));
        if (!problems.isEmpty()) {
            throw new IllegalStateException(problems.size() + " problem(s) between the lessons and the toolkit");
        }
        log.info("Lessons and the toolkit agree: lesson.yaml files, catalogue, lessons index and technique links.");
    }
}
