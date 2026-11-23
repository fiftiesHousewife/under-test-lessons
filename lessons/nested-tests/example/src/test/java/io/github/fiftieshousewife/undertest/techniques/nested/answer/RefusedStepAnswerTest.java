package io.github.fiftieshousewife.undertest.techniques.nested.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.nested.Loan;
import io.github.fiftieshousewife.undertest.techniques.nested.Loans;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** The exercise's answer: one more question for every situation. Does a refused step leave the loan as it was? */
@DisplayName("A loan, after a refused step")
class RefusedStepAnswerTest {

    private final Loan loan = Loans.newLoan();

    /** Takes a step that should be refused; the test then asks what state the loan was left in. */
    private static void tryTo(final Runnable step) {
        try {
            step.run();
        } catch (final IllegalStateException expected) {
            // refused, as it should be: what matters here is the state afterwards
        }
    }

    @Nested
    @DisplayName("when requested")
    class Requested {

        @Test
        @DisplayName("is still requested")
        void isStillRequested() {
            tryTo(loan::repay);
            assertThat(loan.state()).isEqualTo(Loan.State.REQUESTED);
        }
    }

    @Nested
    @DisplayName("when approved")
    class Approved {

        @Test
        @DisplayName("is still approved")
        void isStillApproved() {
            loan.approve();
            tryTo(loan::approve);
            assertThat(loan.state()).isEqualTo(Loan.State.APPROVED);
        }
    }

    @Nested
    @DisplayName("when repaid")
    class Repaid {

        @Test
        @DisplayName("is still repaid")
        void isStillRepaid() {
            loan.approve();
            loan.repay();
            tryTo(loan::repay);
            assertThat(loan.state()).isEqualTo(Loan.State.REPAID);
        }
    }
}
