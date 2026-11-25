package io.github.fiftieshousewife.undertest.techniques.dynamic;

import io.github.fiftieshousewife.undertest.techniques.dynamic.fixed.LetterNameFormatter;
import io.github.fiftieshousewife.undertest.techniques.dynamic.today.TodaysNameFormatter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The formatter the tests run against (the fixed one, or today's with -Pcode=today and -Pcode=answer, which runs the
 * exercise's answer beside today's tests), and the example files.
 */
public final class Formatters {

    private Formatters() {
    }

    public static NameFormatter current() {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysNameFormatter() : new LetterNameFormatter();
    }

    /* Every example file: its first line is a record, its second the name expected on a letter. */
    static List<Path> examples() {
        return examples("/examples");
    }

    /** Every example file in the given folder of the test resources. */
    public static List<Path> examples(final String folder) {
        try (Stream<Path> files = Files.list(Path.of(Formatters.class.getResource(folder).toURI()))) {
            return files.sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<String> lines(final Path example) {
        try {
            return Files.readAllLines(example);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
