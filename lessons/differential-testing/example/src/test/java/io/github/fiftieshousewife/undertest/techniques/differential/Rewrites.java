package io.github.fiftieshousewife.undertest.techniques.differential;

import io.github.fiftieshousewife.undertest.techniques.differential.rewrite.PriceList;
import io.github.fiftieshousewife.undertest.techniques.differential.rewrite.RewrittenPostage;
import java.util.Set;

/*
 * The rewrite the tests run against: with the corrected price list, or as typed today with -Pcode=today (and with
 * -Pcode=answer, which adds the exercise's answer).
 */
final class Rewrites {

    private Rewrites() {
    }

    static Postage current() {
        return new RewrittenPostage(Set.of("today", "answer").contains(System.getProperty("code"))
                ? PriceList.AS_TYPED_TODAY : PriceList.CORRECTED);
    }
}
