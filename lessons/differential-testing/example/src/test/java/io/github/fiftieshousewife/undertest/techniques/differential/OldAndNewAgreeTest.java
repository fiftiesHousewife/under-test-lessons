package io.github.fiftieshousewife.undertest.techniques.differential;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.differential.legacy.LegacyPostage;
import java.util.List;
import java.util.stream.IntStream;
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
        assertThat(disagreements()).as("weights where the rewrite disagrees").isEmpty();
    }

    /* Each run of weights where the two disagree: it starts at a weight that differs after one that doesn't. */
    private List<String> disagreements() {
        return IntStream.rangeClosed(1, Postage.MAX_GRAMS)
                .filter(grams -> differs(grams) && !differs(grams - 1))
                .mapToObj(from -> "%d g to %d g: old %dp, new %dp".formatted(
                        from, lastOfRun(from), legacy.pence(from), rewrite.pence(from)))
                .toList();
    }

    private int lastOfRun(final int from) {
        int to = from;
        while (differs(to + 1)) {
            to++;
        }
        return to;
    }

    private boolean differs(final int grams) {
        return grams >= 1 && grams <= Postage.MAX_GRAMS && legacy.pence(grams) != rewrite.pence(grams);
    }
}
