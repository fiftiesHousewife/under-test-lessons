package io.github.fiftieshousewife.undertest.techniques.antipatterns;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** The register the build names: the committed file, or, with -Pcode=today, none written down yet. */
public final class Registers {

    private Registers() {
    }

    public static Register chosenByTheBuild() {
        final String file = System.getProperty("antiPatterns.register", "");
        if (file.isEmpty()) {
            return Register.empty();
        }
        try {
            return Register.of(Files.readAllLines(Path.of(file)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
