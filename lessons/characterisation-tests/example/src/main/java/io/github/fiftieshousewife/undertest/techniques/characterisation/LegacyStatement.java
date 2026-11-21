package io.github.fiftieshousewife.undertest.techniques.characterisation;

import java.util.List;

/**
 * The statement printer as it has run in production for years, with no tests. Nobody remembers every rule: the
 * characterisation test pins down what it does today, quirks included, before anyone changes it.
 */
public class LegacyStatement implements Statement {

    private static final int OVERDRAFT_FEE = 25;
    private static final int INTEREST_THRESHOLD = 1000;

    @Override
    public String print(final String account, final List<Transaction> transactions) {
        final StringBuilder out = new StringBuilder("Statement for " + account + "\n");
        int balance = 0;
        for (final Transaction t : transactions) {
            balance += t.amount();
            out.append(String.format("%s  %-24s %6d  balance %6d%n", t.date(), t.description(), t.amount(), balance));
            if (balance <= 0 && t.amount() < 0) {
                balance -= OVERDRAFT_FEE;
                out.append(String.format("%s  %-24s %6d  balance %6d%n", t.date(), "Overdraft fee", -OVERDRAFT_FEE,
                        balance));
            }
        }
        if (balance > INTEREST_THRESHOLD) {
            out.append(String.format("Interest earned this month: %d%n", balance / 100));
        }
        return out.append(String.format("Closing balance: %d%n", balance)).toString();
    }
}
