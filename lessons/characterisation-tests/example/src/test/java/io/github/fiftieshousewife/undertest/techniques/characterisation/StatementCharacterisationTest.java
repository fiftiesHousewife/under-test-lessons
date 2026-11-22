package io.github.fiftieshousewife.undertest.techniques.characterisation;

import java.util.List;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.QuietReporter;
import org.junit.jupiter.api.Test;

/**
 * AFTER: a characterisation test. It saves the whole statement the code prints today as the approved file, next to
 * this test, and fails if the output ever differs, line for line. The month below reaches every rule: an overdraft
 * fee (including at exactly zero, a quirk nobody remembered), and interest on a high balance.
 */
class StatementCharacterisationTest {

    private static final List<Transaction> MONTH = List.of(
            new Transaction("01 Mar", "Salary", 1200),
            new Transaction("02 Mar", "Rent", -1200),
            new Transaction("09 Mar", "Groceries", -80),
            new Transaction("15 Mar", "Bonus", 2500),
            new Transaction("20 Mar", "Car insurance", -340));

    @Test
    void printsTheSameStatementAsToday() {
        Approvals.verify(statement().print("J. Smith, current account", MONTH),
                new Options().withReporter(new QuietReporter()).withScrubber(text -> text.replace("\r\n", "\n")));
    }

    /** The legacy printer, or today's rewrite with -Pcode=today. */
    private static Statement statement() {
        return "refactored".equals(System.getProperty("statement.version"))
                ? new RefactoredStatement() : new LegacyStatement();
    }
}
