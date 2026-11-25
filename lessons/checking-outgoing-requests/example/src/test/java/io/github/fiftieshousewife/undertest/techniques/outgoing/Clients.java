package io.github.fiftieshousewife.undertest.techniques.outgoing;

import io.github.fiftieshousewife.undertest.techniques.outgoing.fixed.RefundClient;
import io.github.fiftieshousewife.undertest.techniques.outgoing.today.TodaysRefundClient;
import java.net.URI;
import java.util.Set;

/**
 * The code the tests run against: the fix, or today's with -Pcode=today (and -Pcode=answer, the exercise's). The
 * tests use the shop's test key for the provider, and a refund of £12.50 on order A-1001, with its own id.
 */
public final class Clients {

    static final String TEST_KEY = "test-key";

    static final Refund REFUND = new Refund("RF-5501", "A-1001", 1250);

    private Clients() {
    }

    public static Refunds at(final String baseUrl) {
        final ProviderHttp provider = new ProviderHttp(URI.create(baseUrl), TEST_KEY);
        return today() ? new TodaysRefundClient(provider) : new RefundClient(provider);
    }

    private static boolean today() {
        return Set.of("today", "answer").contains(System.getProperty("code"));
    }
}
