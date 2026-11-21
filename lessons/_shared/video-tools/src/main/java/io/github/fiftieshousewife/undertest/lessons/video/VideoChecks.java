package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Entry point: {@code VideoChecks <repository root>} fails if any lesson video script has a known mistake. */
@Slf4j
public final class VideoChecks {

    private VideoChecks() {
    }

    public static void main(final String... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: VideoChecks <repository root>");
        }
        final List<Problem> problems = new VideoScriptChecker().check(Path.of(args[0]));
        problems.forEach(problem -> log.error("{}", problem.toString().replace('\r', ' ').replace('\n', ' ')));
        if (!problems.isEmpty()) {
            throw new IllegalStateException(problems.size() + " problem(s) in lesson video scripts");
        }
        log.info("Lesson video scripts are in order.");
    }
}
