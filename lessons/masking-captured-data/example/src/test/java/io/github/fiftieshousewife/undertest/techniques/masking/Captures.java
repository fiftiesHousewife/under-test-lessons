package io.github.fiftieshousewife.undertest.techniques.masking;

import io.github.fiftieshousewife.undertest.techniques.masking.fixed.KeyedMasking;
import io.github.fiftieshousewife.undertest.techniques.masking.fixed.KeyedSubstitutes;
import io.github.fiftieshousewife.undertest.techniques.masking.today.TodaysMasking;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;

/** The day's data kept with the tests (made up, as if captured), and the masking the tests use. */
public final class Captures {

    /** A fixed key for the lesson. A real key is kept out of the repository, apart from the masked files. */
    static final byte[] TEST_KEY = "lessons: a fixed test key, never a real one".getBytes(StandardCharsets.UTF_8);

    private Captures() {
    }

    /** The day's data, as it was captured. */
    static Capture capturedDay() {
        return new Capture(file("orders.csv"), file("payments.csv"));
    }

    /** The masking the tests use: keyed substitutes, or today's masking with -Pcode=today. */
    public static Masking masking() throws GeneralSecurityException {
        return "today".equals(System.getProperty("code"))
                ? new TodaysMasking()
                : new KeyedMasking(new KeyedSubstitutes(TEST_KEY));
    }

    /** The names in the day's data, for the detector to look for. */
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
