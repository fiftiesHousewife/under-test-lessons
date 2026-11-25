package io.github.fiftieshousewife.undertest.techniques.differential;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.differential.legacy.LegacyPostage;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/*
 * AFTER: a differential test. It asks the old calculator and the rewrite about every weight, from 1 g to 5 kg, and
 * lists each run of weights where they disagree. The old calculator is the reference: no expected values to write.
 */
class OldAndNewAgreeTest {

    private final Postage legacy = new LegacyPostage();
    private final Postage rewrite = Rewrites.current();

    @Test
    void rewriteAgreesWithTheOldCalculatorOnEveryWeight() {
        final List<String> differences = new ArrayList<>();
        int from = 0;
        for (int grams = 1; grams <= Postage.MAX_GRAMS + 1; grams++) {
            final boolean differs = grams <= Postage.MAX_GRAMS && legacy.pence(grams) != rewrite.pence(grams);
            if (differs && from == 0) {
                from = grams;
            } else if (!differs && from > 0) {
                differences.add("%d g to %d g: old %dp, new %dp".formatted(
                        from, grams - 1, legacy.pence(from), rewrite.pence(from)));
                from = 0;
            }
        }
        assertThat(differences).as("weights where the rewrite disagrees").isEmpty();
    }
}
