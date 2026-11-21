package io.github.fiftieshousewife.undertest.techniques.recorded;

import io.github.fiftieshousewife.undertest.techniques.recorded.fixed.PostcodeClient;
import io.github.fiftieshousewife.undertest.techniques.recorded.today.TodaysPostcodeClient;
import java.net.URI;

/** The client the tests run against: the fixed one, or today's with -Pcode=today. */
final class Clients {

    private Clients() {
    }

    static PostcodeLookup at(final String baseUrl) {
        final SupplierHttp supplier = new SupplierHttp(URI.create(baseUrl));
        return "today".equals(System.getProperty("code"))
                ? new TodaysPostcodeClient(supplier) : new PostcodeClient(supplier);
    }
}
