package io.github.fiftieshousewife.undertest.techniques.differential;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.differential.legacy.LegacyPostage;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/*
 * AFTER: a differential test, one row for each band of the post office's printed price list. Each row asks the old
 * calculator and the rewrite about every weight in its band, and lists each run of weights where they disagree. The
 * old calculator is the reference: no expected prices to write.
 */
class OldAndNewAgreeTest {

    private final Postage legacy = new LegacyPostage();
    private final Postage rewrite = Rewrites.current();

    @ParameterizedTest(name = "{0}", quoteTextArguments = false)
    @CsvSource(textBlock = """
            up to 100 g,         1,  100
            up to 250 g,       101,  250
            up to 500 g,       251,  500
            up to 1 kg,        501, 1000
            up to 1.5 kg,     1001, 1500
            up to 2 kg,       1501, 2000
            up to 2.5 kg,     2001, 2500
            up to 3 kg,       2501, 3000
            up to 3.5 kg,     3001, 3500
            up to 4 kg,       3501, 4000
            up to 4.5 kg,     4001, 4500
            up to 5 kg,       4501, 5000
            """)
    void rewriteAgreesOnEveryWeightInTheBand(final String band, final int lightest, final int heaviest) {
        assertThat(disagreements(lightest, heaviest)).as("weights " + band + " where the rewrite disagrees").isEmpty();
    }

    /* Each run of weights in the band where the two disagree: it starts at the band's first weight, or after a match */
    private List<String> disagreements(final int lightest, final int heaviest) {
        return IntStream.rangeClosed(lightest, heaviest)
                .filter(grams -> differs(grams) && (grams == lightest || !differs(grams - 1)))
                .mapToObj(from -> "%d g to %d g: old %dp, new %dp".formatted(
                        from, lastOfRun(from, heaviest), legacy.pence(from), rewrite.pence(from)))
                .toList();
    }

    private int lastOfRun(final int from, final int heaviest) {
        int to = from;
        while (to < heaviest && differs(to + 1)) {
            to++;
        }
        return to;
    }

    private boolean differs(final int grams) {
        return legacy.pence(grams) != rewrite.pence(grams);
    }
}
