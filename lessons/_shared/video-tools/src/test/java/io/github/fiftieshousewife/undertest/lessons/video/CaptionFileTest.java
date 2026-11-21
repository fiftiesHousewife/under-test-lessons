package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

class CaptionFileTest {

    private static final String LONG = "PIT plants one small bug at a time, reruns the tests, and counts the bugs they"
            + " catch. Line coverage counts the lines a test ran. It cannot tell whether any test checked the result.";

    @Test
    void splitsEachSceneIntoShortCuesThatFillTheScene() {
        final List<Cue> cues = CaptionFile.cues(List.of(new Scene("why", 2, 12)), List.of(LONG));

        assertAll(
                () -> assertThat(cues).hasSizeGreaterThan(1),
                () -> assertThat(cues.getFirst().start()).isEqualTo(2),
                () -> assertThat(cues.getLast().end()).isCloseTo(12, within(1e-9)),
                () -> assertThat(cues).allSatisfy(cue -> assertThat(cue.text().replace("\n", " "))
                        .hasSizeLessThanOrEqualTo(CaptionFile.MAX_CUE_CHARACTERS)),
                () -> assertThat(cues).allSatisfy(cue -> assertThat(cue.text().split("\n")).allSatisfy(
                        line -> assertThat(line).hasSizeLessThanOrEqualTo(CaptionFile.MAX_LINE_CHARACTERS))));
    }

    @Test
    void needsOneParagraphPerScene() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> CaptionFile.cues(List.of(new Scene("a", 0, 1)), List.of()))
                .withMessageContaining("1 scenes but 0 narration paragraphs");
    }

    @Test
    void writesWebVttAndSrt() {
        final List<Cue> cues = List.of(new Cue(1.5, 3_725.25, "Hello"));

        assertAll(
                () -> assertThat(CaptionFile.webVtt(cues))
                        .isEqualTo("WEBVTT\n\n00:00:01.500 --> 01:02:05.250\nHello\n\n"),
                () -> assertThat(CaptionFile.srt(cues)).isEqualTo("1\n00:00:01,500 --> 01:02:05,250\nHello\n\n"));
    }

    @Test
    void wrapsLongCuesNearTheMiddle() {
        assertAll(
                () -> assertThat(CaptionFile.wrap("short")).isEqualTo("short"),
                () -> assertThat(CaptionFile.wrap("aaaa bbbb cccc dddd eeee ffff gggg hhhh iiii jjjj kkkk llll"))
                        .isEqualTo("aaaa bbbb cccc dddd eeee ffff\ngggg hhhh iiii jjjj kkkk llll"),
                () -> assertThat(CaptionFile.wrap("x".repeat(60))).isEqualTo("x".repeat(60)));
    }
}
