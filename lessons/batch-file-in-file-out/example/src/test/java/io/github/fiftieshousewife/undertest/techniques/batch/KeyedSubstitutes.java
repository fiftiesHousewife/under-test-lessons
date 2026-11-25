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
 * A substitute is marked TEST, so it can't pass for a real account, and has 48 bits: two accounts would share one
 * only in files of millions of accounts, where 8 digits would already clash at around ten thousand.
 */
final class KeyedSubstitutes {

    private static final String ALGORITHM = "HmacSHA256";
    private static final String TEST_KEY = "lessons: a fixed test key, kept apart from any real key";
    private static final int UNUSED_BITS = 16;

    private final Mac mac;

    KeyedSubstitutes() throws GeneralSecurityException {
        mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(TEST_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM));
    }

    /* A substitute for an account number, such as TEST-3f9a2c1b7e04: always the same one for the same number. */
    String account(final String account) {
        final byte[] keyed = mac.doFinal(account.getBytes(StandardCharsets.UTF_8));
        return "TEST-%012x".formatted(ByteBuffer.wrap(keyed).getLong() >>> UNUSED_BITS);
    }

    /* The line with the account number in the given field (counting from 0) replaced by its substitute. */
    String line(final String line, final int accountField) {
        final String[] fields = line.split(",", -1);
        fields[accountField] = account(fields[accountField]);
        return String.join(",", fields);
    }
}
