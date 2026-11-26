package io.github.fiftieshousewife.undertest.techniques.masking.testdata;

import static io.github.fiftieshousewife.undertest.techniques.masking.testdata.Masking.masked;

import io.github.fiftieshousewife.undertest.techniques.masking.Capture;
import java.util.Map;

/**
 * TODAY: the test data as it is made today. Every name, email address, account and card number becomes the same
 * fixed text, so the tests can no longer tell customers or accounts apart. And the free-text note on each order is
 * missed.
 */
public final class TodaysMasking implements Masking {

    @Override
    public Capture mask(final Capture capture) {
        return new Capture(
                masked(capture.orders(), Map.of(
                        "customer", REMOVE,
                        "email", REMOVE,
                        "account", REMOVE)),
                masked(capture.payments(), Map.of(
                        "account", REMOVE,
                        "card", REMOVE)));
    }
}
