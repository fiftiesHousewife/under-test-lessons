package io.github.fiftieshousewife.undertest.techniques.masking.testdata;

import static io.github.fiftieshousewife.undertest.techniques.masking.testdata.Masking.masked;

import io.github.fiftieshousewife.undertest.techniques.masking.Capture;
import java.util.Locale;
import java.util.Map;

/**
 * FIXED: every field that identifies a real person gets a keyed substitute, so the same customer or account has the
 * same substitute in every row and in both files. Card numbers and the free-text note, which nothing joins on, become
 * fixed text.
 */
public final class KeyedMasking implements Masking {

    private final KeyedSubstitutes substitute;

    public KeyedMasking(final KeyedSubstitutes substitute) {
        this.substitute = substitute;
    }

    @Override
    public Capture mask(final Capture capture) {
        return new Capture(
                masked(capture.orders(), Map.of(
                        "customer", name -> "Customer " + substitute.of(name),
                        "email", email -> substitute.of(email).toLowerCase(Locale.ROOT) + "@example.invalid",
                        "account", account -> "ACCT-" + substitute.of(account),
                        "note", REMOVE)),
                masked(capture.payments(), Map.of(
                        "account", account -> "ACCT-" + substitute.of(account),
                        "card", REMOVE)));
    }
}
