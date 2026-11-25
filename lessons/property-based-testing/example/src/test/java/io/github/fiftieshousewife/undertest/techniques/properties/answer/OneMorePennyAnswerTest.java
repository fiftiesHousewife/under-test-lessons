package io.github.fiftieshousewife.undertest.techniques.properties.answer;

import static org.assertj.core.api.Assertions.fail;

import io.github.fiftieshousewife.undertest.techniques.properties.BillSplitter;
import io.github.fiftieshousewife.undertest.techniques.properties.Splitters;
import java.util.List;
import java.util.stream.IntStream;
import org.jetbrains.jetCheck.Generator;
import org.jetbrains.jetCheck.PropertyChecker;
import org.jetbrains.jetCheck.PropertyFalsified;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: one more rule, comparing two bills rather than checking one. Adding a penny to the bill must
 * add a penny to exactly one share and leave every other share as it was. Run it against the splitter in use today
 * with {@code -Pcode=answer}: jetCheck finds a bill whose extra penny moves the other shares, and shrinks it.
 */
class OneMorePennyAnswerTest {

    /* A bill before its extra penny: the total in pence, and how many share it. */
    record Before(int totalPence, int people) {
    }

    private static final long REPEATABLE_SEED = 20_240_602L;

    private final BillSplitter splitter = Splitters.current();

    // A fixed seed, so every run tries the same bills, as in SplitterPropertyTest; the assertion is the fail() below.
    @SuppressWarnings({"deprecation", "PMD.TestMustAssert"})
    @Test
    void oneMorePennyChangesOneShareByOnePenny() {
        final Generator<Before> bills = Generator.zipWith(Generator.integers(1, 12), Generator.integers(0, 1_000),
                (people, total) -> new Before(total, people));
        try {
            PropertyChecker.customized().withSeed(REPEATABLE_SEED).forAll(bills, this::onlyOneShareGrows);
        } catch (PropertyFalsified falsified) {
            final Before bill = (Before) falsified.getBreakingValue();
            fail("One more penny moved other shares: " + bill.totalPence() + " pence between " + bill.people()
                    + " people gives " + splitter.split(bill.totalPence(), bill.people()) + ", and one more gives "
                    + splitter.split(bill.totalPence() + 1, bill.people()));
        }
    }

    private boolean onlyOneShareGrows(final Before bill) {
        final List<Integer> before = splitter.split(bill.totalPence(), bill.people());
        final List<Integer> after = splitter.split(bill.totalPence() + 1, bill.people());
        final List<Integer> changes = IntStream.range(0, bill.people()).map(i -> after.get(i) - before.get(i))
                .filter(change -> change != 0).boxed().toList();
        return changes.equals(List.of(1));
    }
}
