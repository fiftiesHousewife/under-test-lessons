package io.github.fiftieshousewife.undertest.techniques.masking.fixed;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Keyed substitutes, made with the JDK's HMAC-SHA256: the same value and key always give the same substitute, so
 * repeats and joins still work. 16 letters carry 64 bits, so two customers sharing one is vanishingly unlikely; 10
 * letters were even odds to clash by 1.2 million values. With the key anyone can trace one back: keep it apart.
 */
public final class KeyedSubstitutes {

    private static final String ALGORITHM = "HmacSHA256";
    private static final int LETTERS_FROM = 8;

    private final Mac mac;

    public KeyedSubstitutes(final byte[] key) throws GeneralSecurityException {
        mac = keyed(key);
    }

    private static Mac keyed(final byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        final Mac hmac = Mac.getInstance(ALGORITHM);
        hmac.init(new SecretKeySpec(key, ALGORITHM));
        return hmac;
    }

    /** Sixteen capital letters standing for the value: always the same ones for the same value. */
    public String of(final String value) {
        final byte[] keyed = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        final StringBuilder letters = new StringBuilder();
        for (int i = 0; i < LETTERS_FROM; i++) {
            letters.append((char) ('A' + ((keyed[i] >> 4) & 0xF))).append((char) ('A' + (keyed[i] & 0xF)));
        }
        return letters.toString();
    }
}
