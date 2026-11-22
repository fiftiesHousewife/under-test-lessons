package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProgressTest {

    @Test
    void reportsStagesScenesAndFiles() {
        final Progress progress = new Progress();

        assertAll(
                () -> assertThat(progress.message("Type 1ms note \"ignored before recording\"")).isEmpty(),
                () -> assertThat(progress.message("Hide"))
                        .contains("setting up off-camera (Gradle, plugins, compiling)"),
                () -> assertThat(progress.message("Show")).contains("recording"),
                () -> assertThat(progress.message("Hide")).isEmpty(),
                () -> assertThat(progress.message("Type 1ms note \"The build: one plugin.\""))
                        .contains("  The build: one plugin."),
                () -> assertThat(progress.message("Type 1ms callout \"Same coverage\"")).contains("  callout"),
                () -> assertThat(progress.message("Type ./gradlew -q :lessons:x:pitest")).contains("  running PIT"),
                () -> assertThat(progress.message("Creating out.gif...")).contains("writing out.gif..."),
                () -> assertThat(progress.message("Sleep 2s")).isEmpty());
    }

    @Test
    void saysWhenVhsHasBeenQuietForAWhile() {
        assertAll(
                () -> assertThat(Progress.heartbeat(0)).isEmpty(),
                () -> assertThat(Progress.heartbeat(19)).isEmpty(),
                () -> assertThat(Progress.heartbeat(40))
                        .isEqualTo(Optional.of("    ...still working (40s since the last step)")));
    }
}
