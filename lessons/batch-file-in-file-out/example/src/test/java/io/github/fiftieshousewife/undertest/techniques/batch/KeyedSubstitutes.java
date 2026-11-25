package io.github.fiftieshousewife.undertest.techniques.batch;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/*
 * Masks a captured file before it is kept with the tests: each account number becomes a keyed substitute, made with
 * the JDK's HMAC-SHA256 and a fixed test key. The same account always gets the same substitute, in every file, so a
 * payment and its settlement line still match after masking. The real number can't be worked out without the key.
 */
final class KeyedSubstitutes {

    private static final String ALGORITHM = "HmacSHA256";
    private static final String TEST_KEY = "lessons: a fixed test key, kept apart from any real key";
    private static final long EIGHT_DIGITS = 100_000_000L;

    private final Mac mac;

    KeyedSubstitutes() throws GeneralSecurityException {
        mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(TEST_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM));
    }

    /* An eight-digit substitute for an account number: always the same one for the same number. */
    String account(final String account) {
        final byte[] keyed = mac.doFinal(account.getBytes(StandardCharsets.UTF_8));
        return "%08d".formatted(Integer.toUnsignedLong(ByteBuffer.wrap(keyed).getInt()) % EIGHT_DIGITS);
    }

    /* The line with the account number in the given field (counting from 0) replaced by its substitute. */
    String line(final String line, final int accountField) {
        final String[] fields = line.split(",", -1);
        fields[accountField] = account(fields[accountField]);
        return String.join(",", fields);
    }
}
