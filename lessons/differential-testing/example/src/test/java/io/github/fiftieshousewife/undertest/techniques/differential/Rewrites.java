package io.github.fiftieshousewife.undertest.techniques.differential;

import io.github.fiftieshousewife.undertest.techniques.differential.rewrite.PriceList;
import io.github.fiftieshousewife.undertest.techniques.differential.rewrite.RewrittenPostage;

/** The rewrite the tests run against: with the corrected price list, or as typed today with -Pcode=today. */
final class Rewrites {

    private Rewrites() {
    }

    static Postage current() {
        return new RewrittenPostage("today".equals(System.getProperty("code"))
                ? PriceList.AS_TYPED_TODAY : PriceList.CORRECTED);
    }
}
