package io.github.fiftieshousewife.undertest.techniques.extensions;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** A JUnit extension: the shop's standard settings before each test, put back after it, and a check nothing leaked. */
final class StandardShopSettings implements BeforeEachCallback, AfterEachCallback {

    private static final Map<String, String> STANDARD =
            Map.of(ShopSettings.PRICES_INCLUDE_VAT, "true", ShopSettings.VAT_PERCENT, "20");
    private static final ExtensionContext.Namespace SAVED =
            ExtensionContext.Namespace.create(StandardShopSettings.class);

    @Override
    public void beforeEach(final ExtensionContext context) {
        context.getStore(SAVED).put(Snapshot.class, new Snapshot(ShopSettings.all()));
        ShopSettings.replaceAll(STANDARD);
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        final Map<String, String> left = ShopSettings.all();
        ShopSettings.replaceAll(savedBefore(context));
        if (!left.equals(STANDARD)) {
            throw new AssertionError("The test left a shop setting changed: " + changes(left));
        }
    }

    private static Map<String, String> savedBefore(final ExtensionContext context) {
        return Objects.requireNonNull(context.getStore(SAVED).remove(Snapshot.class, Snapshot.class)).settings();
    }

    private static String changes(final Map<String, String> left) {
        return STANDARD.keySet().stream().sorted()
                .filter(name -> !STANDARD.get(name).equals(left.get(name)))
                .map(name -> name + " was " + STANDARD.get(name) + ", now " + left.get(name))
                .collect(Collectors.joining("; "));
    }

    /** The settings as they were before a test. */
    private record Snapshot(Map<String, String> settings) {
    }
}
