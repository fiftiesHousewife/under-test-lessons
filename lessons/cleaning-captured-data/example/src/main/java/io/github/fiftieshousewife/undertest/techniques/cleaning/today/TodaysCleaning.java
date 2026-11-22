package io.github.fiftieshousewife.undertest.techniques.cleaning.today;

import io.github.fiftieshousewife.undertest.techniques.cleaning.Capture;
import io.github.fiftieshousewife.undertest.techniques.cleaning.Cleaning;
import java.util.Map;

/**
 * TODAY: the cleaning as done today. Every name, email address, account and card number becomes the same fixed text,
 * so the reports can no longer tell customers or accounts apart. And the free-text note on each order is missed.
 */
public final class TodaysCleaning implements Cleaning {

    @Override
    public Capture clean(final Capture capture) {
        return new Capture(
                capture.orders().cleaned(Map.of(
                        "customer", REMOVE,
                        "email", REMOVE,
                        "account", REMOVE)),
                capture.payments().cleaned(Map.of(
                        "account", REMOVE,
                        "card", REMOVE)));
    }
}
