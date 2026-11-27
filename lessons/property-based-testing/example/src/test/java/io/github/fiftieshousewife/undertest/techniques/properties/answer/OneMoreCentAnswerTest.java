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
 * THE EXERCISE'S ANSWER: one more rule, comparing two bills rather than checking one. Adding a cent to the bill must
 * add a cent to exactly one share and leave every other share as it was. Run it against the splitter in use today
 * with {@code -Pcode=answer}: jetCheck finds a bill whose extra cent moves the other shares, and shrinks it.
 */
class OneMoreCentAnswerTest {

    /* A bill before its extra cent: the total in cents, and how many share it. */
    record Before(int totalCents, int people) {
    }

    private static final long REPEATABLE_SEED = 20_240_602L;

    private final BillSplitter splitter = Splitters.current();

    @SuppressWarnings("deprecation") // a fixed seed, so every run tries the same bills, as in SplitterPropertyTest
    @Test
    void oneMoreCentChangesOneShareByOneCent() {
        final Generator<Before> bills = Generator.zipWith(Generator.integers(1, 12), Generator.integers(0, 1_000),
                (people, total) -> new Before(total, people));
        try {
            PropertyChecker.customized().withSeed(REPEATABLE_SEED).forAll(bills, this::onlyOneShareGrows);
        } catch (PropertyFalsified falsified) {
            final Before bill = (Before) falsified.getBreakingValue();
            fail("One more cent moved other shares: " + bill.totalCents() + " cents between " + bill.people()
                    + " people gives " + splitter.split(bill.totalCents(), bill.people()) + ", and one more gives "
                    + splitter.split(bill.totalCents() + 1, bill.people()));
        }
    }

    private boolean onlyOneShareGrows(final Before bill) {
        final List<Integer> before = splitter.split(bill.totalCents(), bill.people());
        final List<Integer> after = splitter.split(bill.totalCents() + 1, bill.people());
        final List<Integer> changes = IntStream.range(0, bill.people()).map(i -> after.get(i) - before.get(i))
                .filter(change -> change != 0).boxed().toList();
        return changes.equals(List.of(1));
    }
}
