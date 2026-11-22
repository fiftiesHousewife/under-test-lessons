package io.github.fiftieshousewife.undertest.techniques.recorded;

import io.github.fiftieshousewife.undertest.techniques.recorded.fixed.KeyedRecordingCleaner;
import io.github.fiftieshousewife.undertest.techniques.recorded.fixed.PostcodeClient;
import io.github.fiftieshousewife.undertest.techniques.recorded.today.TodaysPostcodeClient;
import io.github.fiftieshousewife.undertest.techniques.recorded.today.TodaysRecordingCleaner;
import java.net.URI;

/** The code the tests run against: the fix, or today's with -Pcode=today. */
final class Clients {

    /** A fixed key, for tests only: the same name gets the same substitute on every run. */
    private static final String TEST_KEY = "a key for test recordings only";

    private Clients() {
    }

    static PostcodeLookup at(final String baseUrl) {
        final SupplierHttp supplier = new SupplierHttp(URI.create(baseUrl));
        return today() ? new TodaysPostcodeClient(supplier) : new PostcodeClient(supplier);
    }

    static RecordingCleaner cleaner() {
        return today() ? new TodaysRecordingCleaner() : new KeyedRecordingCleaner(TEST_KEY);
    }

    private static boolean today() {
        return "today".equals(System.getProperty("code"));
    }
}
