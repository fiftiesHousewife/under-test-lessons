package io.github.fiftieshousewife.undertest.techniques.cleaning.fixed;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.fiftieshousewife.undertest.techniques.cleaning.Capture;
import io.github.fiftieshousewife.undertest.techniques.cleaning.Cleaning;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.datafaker.Faker;

/**
 * Seeded synthetic values: each name, email address and account becomes a realistic made-up one from Datafaker, with
 * a fixed seed so every run gives the same ones. A table remembers each choice, so a value repeated anywhere in the
 * capture gets the same made-up value. That table has to be kept to clean the next capture the same way.
 */
public final class SyntheticCleaning implements Cleaning {

    private final long seed;

    public SyntheticCleaning(final long seed) {
        this.seed = seed;
    }

    @Override
    @SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "A fixed seed: every run makes the same values")
    public Capture clean(final Capture capture) {
        final Faker faker = new Faker(new Random(seed));
        final UnaryOperator<String> name = remembered(() -> faker.name().fullName());
        final UnaryOperator<String> email = remembered(() -> emailFor(faker));
        final UnaryOperator<String> account = remembered(() -> "TEST" + faker.number().digits(8));
        return new Capture(
                capture.orders().cleaned(Map.of("customer", name, "email", email, "account", account, "note", REMOVE)),
                capture.payments().cleaned(Map.of("account", account, "card", REMOVE)));
    }

    private static String emailFor(final Faker faker) {
        final String name = faker.name().firstName() + "." + faker.name().lastName();
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z.]", "") + "@example.com";
    }

    private static UnaryOperator<String> remembered(final Supplier<String> madeUp) {
        final Map<String, String> chosen = new HashMap<>();
        return value -> chosen.computeIfAbsent(value, real -> madeUp.get());
    }
}
