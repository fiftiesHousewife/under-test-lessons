package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;

/** Something wrong in a lesson's scene file, slides or narration, and where. */
public record Problem(Path file, String message) {

    @Override
    public String toString() {
        return file + ": " + message;
    }
}
