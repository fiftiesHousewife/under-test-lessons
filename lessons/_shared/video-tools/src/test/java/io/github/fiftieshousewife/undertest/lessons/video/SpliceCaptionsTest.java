package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class SpliceCaptionsTest {

    @Test
    void timesSlideCaptionsAcrossEachSegmentLeavingTheBreathSilent() {
        final List<Slide> slides = List.of(new Slide("One", List.of(), "First slide."),
                new Slide("Two", List.of(), "Second slide."));
        final List<Splicer.Segment> segments = List.of(new Splicer.Segment(Path.of("a"), 4),
                new Splicer.Segment(Path.of("b"), 6));
        final List<Cue> cues = SpliceCaptions.slideCues(slides, segments, 10);

        assertAll(
                () -> assertThat(cues).extracting(Cue::text).containsExactly("First slide.", "Second slide."),
                () -> assertThat(cues.getFirst().start()).isEqualTo(10),
                () -> assertThat(cues.getFirst().end()).isEqualTo(12.5),
                () -> assertThat(cues.get(1).start()).isEqualTo(14),
                () -> assertThat(new Splicer.Timings(segments, List.of()).introSeconds()).isEqualTo(10));
    }
}
