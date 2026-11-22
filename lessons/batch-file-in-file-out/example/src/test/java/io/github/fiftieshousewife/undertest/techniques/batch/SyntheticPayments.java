package io.github.fiftieshousewife.undertest.techniques.batch;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.datafaker.Faker;

/** A day's payments made up by Datafaker: realistic, never real, and the same on every run, from a fixed seed. */
final class SyntheticPayments {

    private static final long SEED = 42L;
    private static final int MOST_PENCE = 250_000;

    private SyntheticPayments() {
    }

    @SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "A fixed seed, so runs make the same payments")
    static List<Payment> forTheDay(final int count) {
        final Faker faker = new Faker(new Random(SEED));
        return Stream.generate(() -> new Payment(faker.number().digits(8), faker.number().numberBetween(1, MOST_PENCE)))
                .limit(count).toList();
    }

    /** Writes the payments as the job's input file, one {@code account,pence} line each. */
    static Path write(final List<Payment> payments, final Path file) throws IOException {
        return Files.write(file, payments.stream().map(payment -> payment.account() + "," + payment.pence()).toList(),
                StandardCharsets.UTF_8);
    }
}
