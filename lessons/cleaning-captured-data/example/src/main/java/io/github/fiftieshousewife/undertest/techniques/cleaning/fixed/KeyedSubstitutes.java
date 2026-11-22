package io.github.fiftieshousewife.undertest.techniques.cleaning.fixed;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Keyed substitutes, made with the JDK's HMAC-SHA256: the same value and the same key always give the same
 * substitute, so repeated values and joins between files still work. Without the key, a substitute can't be traced
 * back. With it, anyone can, by trying every likely value: so the key is kept apart from the cleaned files.
 */
public final class KeyedSubstitutes {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int LETTERS_FROM = 5;

    private final Mac mac;

    public KeyedSubstitutes(final byte[] key) throws GeneralSecurityException {
        mac = keyed(key);
    }

    private static Mac keyed(final byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        final Mac hmac = Mac.getInstance(ALGORITHM);
        hmac.init(new SecretKeySpec(key, ALGORITHM));
        return hmac;
    }

    /** Ten capital letters standing for the value: always the same ones for the same value. */
    public String of(final String value) {
        final byte[] keyed = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        final StringBuilder letters = new StringBuilder();
        for (int i = 0; i < LETTERS_FROM; i++) {
            letters.append((char) ('A' + ((keyed[i] >> 4) & 0xF))).append((char) ('A' + (keyed[i] & 0xF)));
        }
        return letters.toString();
    }
}
