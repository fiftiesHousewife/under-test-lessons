package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** What the short check recording must report before any lesson is recorded. */
class ZoomCheckTest {

    @Test
    void passesThreeLinesOnScreenAndSaysWhatIsWrongOtherwise() {
        assertAll(
                () -> assertThat(ZoomCheck.problem(List.of(Optional.of(new Zoom.Rows(8, 10, 40))))).isEmpty(),
                () -> assertThat(ZoomCheck.problem(List.of())).hasValueSatisfying(
                        problem -> assertThat(problem).contains("got 0").contains("VIDEO_ZOOMS")),
                () -> assertThat(ZoomCheck.problem(List.of(Optional.empty()))).hasValueSatisfying(
                        problem -> assertThat(problem).contains("didn't say where the cursor was")),
                () -> assertThat(ZoomCheck.problem(List.of(Optional.of(new Zoom.Rows(8, 12, 40))))).isPresent(),
                () -> assertThat(ZoomCheck.problem(List.of(Optional.of(new Zoom.Rows(39, 41, 40))))).isPresent());
    }

    @Test
    void theCheckRecordingsZoomHasTimeToRun() {
        final java.nio.file.Path root = java.nio.file.Path.of("../../..").toAbsolutePath().normalize();

        assertAll(
                () -> assertThat(ZoomChecks.problems(root, root.resolve(ZoomCheck.TAPE))).isEmpty(),
                () -> assertThat(TapeTimeline.zooms(root, root.resolve(ZoomCheck.TAPE), List.of())).hasSize(1));
    }
}
