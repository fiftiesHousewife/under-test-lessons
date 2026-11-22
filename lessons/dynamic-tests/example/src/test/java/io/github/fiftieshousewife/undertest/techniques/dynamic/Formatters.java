package io.github.fiftieshousewife.undertest.techniques.dynamic;

import io.github.fiftieshousewife.undertest.techniques.dynamic.fixed.LetterNameFormatter;
import io.github.fiftieshousewife.undertest.techniques.dynamic.today.TodaysNameFormatter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/** The formatter the tests run against (the fixed one, or today's with -Pcode=today), and the example files. */
final class Formatters {

    private Formatters() {
    }

    static NameFormatter current() {
        return "today".equals(System.getProperty("code")) ? new TodaysNameFormatter() : new LetterNameFormatter();
    }

    /** Every example file: its first line is a record, its second the name expected on a letter. */
    static List<Path> examples() {
        try (Stream<Path> files = Files.list(Path.of(Formatters.class.getResource("/examples").toURI()))) {
            return files.sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    static List<String> lines(final Path example) {
        try {
            return Files.readAllLines(example);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
