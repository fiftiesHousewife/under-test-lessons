package io.github.fiftieshousewife.undertest.techniques.outgoing;

import io.github.fiftieshousewife.undertest.techniques.outgoing.fixed.RefundClient;
import io.github.fiftieshousewife.undertest.techniques.outgoing.today.TodaysRefundClient;
import java.net.URI;

/** The code the tests run against: the fix, or today's with -Pcode=today. */
final class Clients {

    /** The shop's key for the provider, for tests only. */
    static final String TEST_KEY = "test-key";

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
