package io.github.fiftieshousewife.undertest.techniques.nested;

import io.github.fiftieshousewife.undertest.techniques.nested.fixed.FixedLoan;
import io.github.fiftieshousewife.undertest.techniques.nested.today.TodaysLoan;

/** The loan the tests run against: the fixed one, or today's with -Pcode=today. */
final class Loans {

    private Loans() {
    }

    static Loan newLoan() {
        return "today".equals(System.getProperty("code")) ? new TodaysLoan() : new FixedLoan();
    }
}
