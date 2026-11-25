package io.github.fiftieshousewife.undertest.techniques.flags;

import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.OpenFeatureAPI;
import dev.openfeature.sdk.providers.memory.Flag;
import dev.openfeature.sdk.providers.memory.InMemoryProvider;
import io.github.fiftieshousewife.undertest.techniques.flags.fixed.NewShipping;
import io.github.fiftieshousewife.undertest.techniques.flags.today.TodaysNewShipping;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Checkouts for the tests, with the fixed new rules or today's (-Pcode=today, and -Pcode=answer, which runs the
 * exercise's answer beside today's tests), and the flag set either way.
 */
public final class Checkouts {

    private static final AtomicInteger DOMAINS = new AtomicInteger();

    private Checkouts() {
    }

    /* A checkout with no flag set anywhere, so the flag's default applies. */
    static Checkout withDefaultFlags() {
        return checkout(new InMemoryProvider(Map.of()));
    }

    /** A checkout with the "new-shipping" flag switched on or off. */
    public static Checkout withNewShipping(final boolean on) {
        final Flag<Boolean> flag = Flag.<Boolean>builder().variant("on", true).variant("off", false)
                .defaultVariant(on ? "on" : "off").build();
        return checkout(new InMemoryProvider(Map.of("new-shipping", flag)));
    }

    /* Each checkout gets a domain of its own, so tests never share a provider, even when they run at once. */
    private static Checkout checkout(final FeatureProvider provider) {
        final String domain = "checkout-" + DOMAINS.incrementAndGet();
        OpenFeatureAPI.getInstance().setProviderAndWait(domain, provider);
        final ShippingRules replacement = Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysNewShipping() : new NewShipping();
        return new Checkout(OpenFeatureAPI.getInstance().getClient(domain), new ClassicShipping(), replacement);
    }
}
