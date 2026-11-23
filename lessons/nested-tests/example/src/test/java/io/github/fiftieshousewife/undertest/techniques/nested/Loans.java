package io.github.fiftieshousewife.undertest.techniques.nested;

import io.github.fiftieshousewife.undertest.techniques.nested.fixed.FixedLoan;
import io.github.fiftieshousewife.undertest.techniques.nested.today.TodaysLoan;
import java.util.Set;

/**
 * The loan the tests run against: the fixed one, or today's with -Pcode=today (and with -Pcode=answer, which runs
 * the exercise's answer beside today's tests).
 */
public final class Loans {

    private Loans() {
    }

    public static Loan newLoan() {
        return Set.of("today", "answer").contains(System.getProperty("code")) ? new TodaysLoan() : new FixedLoan();
    }
}
