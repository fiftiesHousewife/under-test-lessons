package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;

/** Something wrong in a video script, and where. */
public record Problem(Path file, String message) {

    @Override
    public String toString() {
        return file + ": " + message;
    }
}
