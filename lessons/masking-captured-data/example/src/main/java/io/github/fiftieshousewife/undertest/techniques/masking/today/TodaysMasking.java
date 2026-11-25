package io.github.fiftieshousewife.undertest.techniques.masking.today;

import io.github.fiftieshousewife.undertest.techniques.masking.Capture;
import io.github.fiftieshousewife.undertest.techniques.masking.Masking;
import java.util.Map;

/**
 * TODAY: the masking as done today. Every name, email address, account and card number becomes the same fixed text,
 * so the reports can no longer tell customers or accounts apart. And the free-text note on each order is missed.
 */
public final class TodaysMasking implements Masking {

    @Override
    public Capture mask(final Capture capture) {
        return new Capture(
                capture.orders().masked(Map.of(
                        "customer", REMOVE,
                        "email", REMOVE,
                        "account", REMOVE)),
                capture.payments().masked(Map.of(
                        "account", REMOVE,
                        "card", REMOVE)));
    }
}
