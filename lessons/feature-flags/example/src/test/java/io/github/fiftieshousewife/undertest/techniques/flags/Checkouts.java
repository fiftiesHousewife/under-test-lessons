package io.github.fiftieshousewife.undertest.techniques.flags;

import dev.openfeature.sdk.OpenFeatureAPI;
import dev.openfeature.sdk.providers.memory.Flag;
import dev.openfeature.sdk.providers.memory.InMemoryProvider;
import io.github.fiftieshousewife.undertest.techniques.flags.fixed.NewShipping;
import io.github.fiftieshousewife.undertest.techniques.flags.today.TodaysNewShipping;
import java.util.Map;
import java.util.Set;

/**
 * Checkouts for the tests, with the fixed new rules or today's (-Pcode=today, and -Pcode=answer, which runs the
 * exercise's answer beside today's tests), and the flag set either way.
 */
public final class Checkouts {

    private Checkouts() {
    }

    /** A checkout with no flag set anywhere, so the flag's default applies. */
    static Checkout withDefaultFlags() {
        OpenFeatureAPI.getInstance().setProviderAndWait(new InMemoryProvider(Map.of()));
        return checkout();
    }

    /** A checkout with the "new-shipping" flag switched on or off. */
    public static Checkout withNewShipping(final boolean on) {
        final Flag<Boolean> flag = Flag.<Boolean>builder().variant("on", true).variant("off", false)
                .defaultVariant(on ? "on" : "off").build();
        OpenFeatureAPI.getInstance().setProviderAndWait(new InMemoryProvider(Map.of("new-shipping", flag)));
        return checkout();
    }

    private static Checkout checkout() {
        final ShippingRules replacement = Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysNewShipping() : new NewShipping();
        return new Checkout(OpenFeatureAPI.getInstance().getClient(), new ClassicShipping(), replacement);
    }
}
