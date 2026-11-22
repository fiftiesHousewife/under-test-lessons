package io.github.fiftieshousewife.undertest.techniques.differential.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.differential.Postage;
import io.github.fiftieshousewife.undertest.techniques.differential.legacy.LegacyPostage;
import io.github.fiftieshousewife.undertest.techniques.differential.rewrite.PriceList;
import io.github.fiftieshousewife.undertest.techniques.differential.rewrite.RewrittenPostage;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * THE EXERCISE'S ANSWER: a differential test compares failures as well as answers. Outside 1 g to 5 kg the old
 * calculator refuses the parcel, so the rewrite must refuse the same weights, with the same message. Run it against
 * today's rewrite with {@code -Pcode=answer}: the refusals agree, so the price gap is the only difference.
 */
class RefusalsAgreeAnswerTest {

    private static final List<Integer> OUTSIDE_THE_RANGE = List.of(-1, 0, Postage.MAX_GRAMS + 1, 10_000);

    @Test
    void rewriteRefusesTheSameWeightsAsTheOldCalculator() {
        final Postage rewrite = new RewrittenPostage(Set.of("today", "answer").contains(System.getProperty("code"))
                ? PriceList.AS_TYPED_TODAY : PriceList.CORRECTED);
        final Postage legacy = new LegacyPostage();

        assertThat(OUTSIDE_THE_RANGE.stream().map(grams -> outcome(rewrite, grams)).toList())
                .as("what the rewrite does outside the range")
                .isEqualTo(OUTSIDE_THE_RANGE.stream().map(grams -> outcome(legacy, grams)).toList());
    }

    /** The price, or why the parcel was refused. */
    private static String outcome(final Postage postage, final int grams) {
        try {
            return grams + " g: " + postage.pence(grams) + "p";
        } catch (IllegalArgumentException e) {
            return grams + " g: refused, " + e.getMessage();
        }
    }
}
