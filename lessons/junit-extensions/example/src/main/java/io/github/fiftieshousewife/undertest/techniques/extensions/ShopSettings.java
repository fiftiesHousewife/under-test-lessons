package io.github.fiftieshousewife.undertest.techniques.extensions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** The shop's settings, shared by the whole program, as old systems often keep them. Anything can change them. */
public final class ShopSettings {

    public static final String PRICES_INCLUDE_VAT = "prices.include.vat";
    public static final String VAT_PERCENT = "vat.percent";

    private static final Map<String, String> SETTINGS =
            new ConcurrentHashMap<>(Map.of(PRICES_INCLUDE_VAT, "true", VAT_PERCENT, "20"));

    private ShopSettings() {
    }

    public static String get(final String name) {
        return SETTINGS.get(name);
    }

    public static void set(final String name, final String value) {
        SETTINGS.put(name, value);
    }

    /** A copy of every setting, as it is now. */
    public static Map<String, String> all() {
        return Map.copyOf(SETTINGS);
    }

    /** Replaces every setting with the given ones. */
    public static void replaceAll(final Map<String, String> settings) {
        SETTINGS.clear();
        SETTINGS.putAll(settings);
    }
}
