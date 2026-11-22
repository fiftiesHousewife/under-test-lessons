package io.github.fiftieshousewife.undertest.lessons.checks;

import java.nio.file.Path;

/** One thing wrong with one file. */
public record Problem(Path file, String message) {

    @Override
    public String toString() {
        return file + ": " + message;
    }
}
