package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the recording step with a stand-in for VHS. */
class RecorderTest {

    @TempDir
    Path root;

    @AfterEach
    void restore() {
        System.clearProperty("video.vhs");
    }

    private void standInVhs(final String body) throws IOException {
        final Path script = root.resolve("vhs");
        Files.writeString(script, "#!/bin/sh\n" + body + "\n");
        assertThat(script.toFile().setExecutable(true)).isTrue();
        System.setProperty("video.vhs", script.toString());
    }

    @Test
    void recordsWhenVhsSucceedsAndReportsTheEndOfItsLogWhenItFails() throws IOException {
        final Media media = new Media(root, "demo");
        standInVhs("printf 'Hide\\nShow\\nType 1ms note \"hello\"\\nCreating demo.gif...\\n'");
        assertThatCode(() -> new Recorder(Clock.systemUTC()).record(media)).doesNotThrowAnyException();

        standInVhs("echo 'Error: timeout waiting for Killed'; exit 1");
        assertAll(
                () -> assertThatIllegalStateException().isThrownBy(() -> new Recorder(Clock.systemUTC()).record(media))
                        .withMessageContaining("Recording failed")
                        .withMessageContaining("timeout waiting for Killed"),
                () -> assertThat(Recorder.vhs()).endsWith("vhs"));
    }
}
