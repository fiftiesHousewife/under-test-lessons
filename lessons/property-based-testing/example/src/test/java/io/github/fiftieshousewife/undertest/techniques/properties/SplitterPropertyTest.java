package io.github.fiftieshousewife.undertest.techniques.properties;

import static org.assertj.core.api.Assertions.fail;

import java.util.Collections;
import java.util.List;
import org.jetbrains.jetCheck.Generator;
import org.jetbrains.jetCheck.PropertyChecker;
import org.jetbrains.jetCheck.PropertyFalsified;
import org.junit.jupiter.api.Test;

/*
 * AFTER: a property test. Instead of single examples, it states rules that must hold for every bill, and jetCheck
 * tries a hundred random bills. If one breaks a rule, jetCheck shrinks it to a smaller bill that still does. The seed
 * is fixed, so every run tries the same bills and the lesson is repeatable.
 */
class SplitterPropertyTest {

    /* A bill to split: the total in pence, and the number of people. */
    record Bill(int totalPence, int people) {
    }

    private static final long SEED = 20_240_601L;

    private final BillSplitter splitter = Splitters.current();

    // jetCheck deprecates a fixed seed to steer real projects towards random seeds, which find more over time. A lesson
    // must give the same result every run, so this one keeps a fixed seed.
    @SuppressWarnings("deprecation")
    @Test
    void sharesAddUpToTheTotalAndDifferByAtMostOnePenny() {
        try {
            PropertyChecker.customized().withSeed(SEED).forAll(Generator.zipWith(Generator.integers(0, 1_000),
                    Generator.integers(1, 12), Bill::new), this::isFair);
        } catch (PropertyFalsified falsified) {
            final Bill bill = (Bill) falsified.getBreakingValue();
            fail("Unfair bill found: " + bill.totalPence() + " pence between " + bill.people() + " people gives "
                    + splitter.split(bill.totalPence(), bill.people()));
        }
    }

    private boolean isFair(final Bill bill) {
        final List<Integer> shares = splitter.split(bill.totalPence(), bill.people());
        return shares.size() == bill.people()
                && shares.stream().mapToInt(Integer::intValue).sum() == bill.totalPence()
                && Collections.max(shares) - Collections.min(shares) <= 1;
    }
}
