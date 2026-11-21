package io.github.fiftieshousewife.undertest.techniques.characterisation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Pins down the one rule the rewrite changed, so the difference is written down while the Application Owner decides:
 * at a balance of exactly zero, the legacy printer charges an overdraft fee and the rewrite doesn't.
 */
class RefactoredStatementTest {

    private static final List<Transaction> TO_ZERO = List.of(
            new Transaction("01 Mar", "Salary", 1200), new Transaction("02 Mar", "Rent", -1200));

    @Test
    void chargesNoFeeAtExactlyZeroUnlikeTheLegacyPrinter() {
        assertAll(
                () -> assertThat(new LegacyStatement().print("J. Smith", TO_ZERO)).contains("Overdraft fee"),
                () -> assertThat(new RefactoredStatement().print("J. Smith", TO_ZERO)).doesNotContain("Overdraft fee")
                        .contains("Closing balance: 0"));
    }

    @Test
    void stillChargesTheFeeBelowZeroAndPaysInterestOnHighBalances() {
        final String statement = new RefactoredStatement().print("J. Smith", List.of(
                new Transaction("01 Mar", "Rent", -100), new Transaction("02 Mar", "Bonus", 2000)));

        assertAll(
                () -> assertThat(statement).contains("Overdraft fee"),
                () -> assertThat(statement).contains("Interest earned this month: 18"));
    }
}
