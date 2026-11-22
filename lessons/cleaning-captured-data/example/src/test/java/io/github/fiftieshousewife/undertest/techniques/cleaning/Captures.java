package io.github.fiftieshousewife.undertest.techniques.cleaning;

import io.github.fiftieshousewife.undertest.techniques.cleaning.fixed.KeyedCleaning;
import io.github.fiftieshousewife.undertest.techniques.cleaning.fixed.KeyedSubstitutes;
import io.github.fiftieshousewife.undertest.techniques.cleaning.today.TodaysCleaning;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;

/** The captured day kept with the tests (made up, as if captured), and the cleaning the tests use. */
final class Captures {

    /** A fixed key for the lesson. A real key is kept out of the repository, apart from the cleaned files. */
    static final byte[] TEST_KEY = "lessons: a fixed test key, never a real one".getBytes(StandardCharsets.UTF_8);

    private Captures() {
    }

    /** The captured day, as it was captured. */
    static Capture capturedDay() {
        return new Capture(file("orders.csv"), file("payments.csv"));
    }

    /** The cleaning the tests use: keyed substitutes, or today's cleaning with -Pcode=today. */
    static Cleaning cleaning() throws GeneralSecurityException {
        return "today".equals(System.getProperty("code"))
                ? new TodaysCleaning()
                : new KeyedCleaning(new KeyedSubstitutes(TEST_KEY));
    }

    /** The names in the captured day, for the detector to look for. */
    static List<String> customerNames() {
        return capturedDay().orders().column("customer");
    }

    private static CapturedFile file(final String name) {
        try (InputStream in = Captures.class.getResourceAsStream("/capture/" + name)) {
            final String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return CapturedFile.parse(name, text.lines().toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
