package io.github.fiftieshousewife.undertest.techniques.cleaning.fixed;

import io.github.fiftieshousewife.undertest.techniques.cleaning.Capture;
import io.github.fiftieshousewife.undertest.techniques.cleaning.Cleaning;
import java.util.Locale;
import java.util.Map;

/**
 * FIXED: every field that identifies someone gets a keyed substitute, so the same customer or account has the same
 * substitute in every row and in both files. Card numbers and the free-text note, which nothing joins on, become fixed
 * text.
 */
public final class KeyedCleaning implements Cleaning {

    private final KeyedSubstitutes substitute;

    public KeyedCleaning(final KeyedSubstitutes substitute) {
        this.substitute = substitute;
    }

    @Override
    public Capture clean(final Capture capture) {
        return new Capture(
                capture.orders().cleaned(Map.of(
                        "customer", name -> "Customer " + substitute.of(name),
                        "email", email -> substitute.of(email).toLowerCase(Locale.ROOT) + "@example.invalid",
                        "account", account -> "ACCT-" + substitute.of(account),
                        "note", REMOVE)),
                capture.payments().cleaned(Map.of(
                        "account", account -> "ACCT-" + substitute.of(account),
                        "card", REMOVE)));
    }
}
