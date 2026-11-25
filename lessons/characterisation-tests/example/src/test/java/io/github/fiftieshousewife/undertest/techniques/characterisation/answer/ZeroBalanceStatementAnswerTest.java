package io.github.fiftieshousewife.undertest.techniques.characterisation.answer;

import io.github.fiftieshousewife.undertest.techniques.characterisation.LegacyStatement;
import io.github.fiftieshousewife.undertest.techniques.characterisation.RefactoredStatement;
import io.github.fiftieshousewife.undertest.techniques.characterisation.Statement;
import io.github.fiftieshousewife.undertest.techniques.characterisation.Transaction;
import java.util.List;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.QuietReporter;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: a second, smaller approval that pins down one behaviour, the overdraft fee charged when the
 * balance reaches exactly zero. When the rewrite changes that rule, this approval's difference is three lines, the
 * fee and the balances it changes, rather than every balance after it. Run it against the rewrite with
 * {@code -Pcode=answer}.
 */
class ZeroBalanceStatementAnswerTest {

    private static final List<Transaction> TO_ZERO = List.of(
            new Transaction("01 Mar", "Salary", 1200), new Transaction("02 Mar", "Rent", -1200));

    @Test
    void chargesTheFeeWhenTheBalanceReachesExactlyZero() {
        final Statement statement = "refactored".equals(System.getProperty("statement.version"))
                ? new RefactoredStatement() : new LegacyStatement();
        Approvals.verify(statement.print("J. Smith", TO_ZERO),
                new Options().withReporter(new QuietReporter()).withScrubber(text -> text.replace("\r\n", "\n")));
    }
}
