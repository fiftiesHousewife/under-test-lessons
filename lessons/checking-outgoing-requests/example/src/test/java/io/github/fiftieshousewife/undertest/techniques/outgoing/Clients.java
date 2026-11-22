package io.github.fiftieshousewife.undertest.techniques.outgoing;

import io.github.fiftieshousewife.undertest.techniques.outgoing.fixed.RefundClient;
import io.github.fiftieshousewife.undertest.techniques.outgoing.today.TodaysRefundClient;
import java.net.URI;

/** The code the tests run against: the fix, or today's with -Pcode=today. */
final class Clients {

    /** The shop's key for the provider, for tests only. */
    static final String TEST_KEY = "test-key";

    /** A refund of £12.50 on order A-1001, with its own id. */
    static final Refund REFUND = new Refund("RF-5501", "A-1001", 1250);

    private Clients() {
    }

    static Refunds at(final String baseUrl) {
        final ProviderHttp provider = new ProviderHttp(URI.create(baseUrl), TEST_KEY);
        return today() ? new TodaysRefundClient(provider) : new RefundClient(provider);
    }

    private static boolean today() {
        return "today".equals(System.getProperty("code"));
    }
}
