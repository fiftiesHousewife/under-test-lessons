package io.github.fiftieshousewife.undertest.techniques.characterisation;

import java.util.List;

/**
 * A tidier rewrite of {@link LegacyStatement}, as someone might write it while cleaning up. It reads better, but one
 * rule quietly changed: the overdraft fee now applies only below zero, not at exactly zero. The characterisation test
 * catches this; whether it is a fix or a bug is a question for the Application Owner.
 */
public class RefactoredStatement implements Statement {

    private static final int OVERDRAFT_FEE = 25;
    private static final int INTEREST_THRESHOLD = 1000;

    @Override
    public String print(final String account, final List<Transaction> transactions) {
        final StringBuilder out = new StringBuilder("Statement for " + account + "\n");
        int balance = 0;
        for (final Transaction transaction : transactions) {
            balance += transaction.amount();
            line(out, transaction, balance);
            if (balance < 0 && transaction.amount() < 0) {
                balance -= OVERDRAFT_FEE;
                line(out, new Transaction(transaction.date(), "Overdraft fee", -OVERDRAFT_FEE), balance);
            }
        }
        if (balance > INTEREST_THRESHOLD) {
            out.append(String.format("Interest earned this month: %d%n", balance / 100));
        }
        return out.append(String.format("Closing balance: %d%n", balance)).toString();
    }

    private static void line(final StringBuilder out, final Transaction transaction, final int balance) {
        out.append(String.format("%s  %-24s %6d  balance %6d%n", transaction.date(), transaction.description(),
                transaction.amount(), balance));
    }
}
