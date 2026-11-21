package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class PausesTest {

    @Test
    void splitsNarrationAtPauseMarkers() {
        final String narration = "Why do we need it? [pause 1s] Because coverage counts lines. [pause 500ms]"
                + " That is all.";

        assertAll(
                () -> assertThat(Pauses.parts(narration)).containsExactly(
                        new Pauses.Part("Why do we need it?", 1),
                        new Pauses.Part("Because coverage counts lines.", 0.5),
                        new Pauses.Part("That is all.", 0)),
                () -> assertThat(Pauses.withoutMarkers(narration))
                        .isEqualTo("Why do we need it? Because coverage counts lines. That is all."),
                () -> assertThat(Pauses.parts("No pauses here."))
                        .containsExactly(new Pauses.Part("No pauses here.", 0)));
    }
}
