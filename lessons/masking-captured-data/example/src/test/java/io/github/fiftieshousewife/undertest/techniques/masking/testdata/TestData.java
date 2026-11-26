package io.github.fiftieshousewife.undertest.techniques.masking.testdata;

import io.github.fiftieshousewife.undertest.techniques.masking.Capture;
import io.github.fiftieshousewife.undertest.techniques.masking.CapturedFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * The tests' data: a captured day of the shop's real data (made up for the lesson, as if captured), masked before the
 * tests use it. The masking is today's, with {@code -Pcode=today}, or keyed substitutes.
 */
public final class TestData {

    private static final byte[] TEST_KEY =
            // A fixed key for the lesson. A real key is kept out of the repository, apart from the masked files.
            "lessons: a fixed test key, never a real one".getBytes(StandardCharsets.UTF_8);

    private TestData() {
    }

    /** A captured day, from the folder of that name in the test resources, masked as the tests use it. */
    public static Capture masked(final String day) {
        return masking().mask(captured(day));
    }

    /** The names in a captured day, for the guard to look for. */
    public static List<String> customerNames(final String day) {
        return captured(day).orders().column("customer");
    }

    private static Masking masking() {
        if ("today".equals(System.getProperty("code"))) {
            return new TodaysMasking();
        }
        try {
            return new KeyedMasking(new KeyedSubstitutes(TEST_KEY));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("this JDK can't make keyed substitutes", e);
        }
    }

    private static Capture captured(final String day) {
        return new Capture(file(day, "orders.csv"), file(day, "payments.csv"));
    }

    private static CapturedFile file(final String day, final String name) {
        try (InputStream in = TestData.class.getResourceAsStream("/" + day + "/" + name)) {
            if (in == null) {
                throw new IllegalArgumentException("no captured file " + day + "/" + name);
            }
            return CapturedFile.parse(name, new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
