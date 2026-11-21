package io.github.fiftieshousewife.undertest.techniques.nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.junit.jupiter.api.Test;

/** BEFORE: one flat list of tests. Each name packs in a situation and a result, and nothing shows what is missing. */
class FlatLoanTest {

    @Test
    void newLoanIsRequested() {
        assertThat(Loans.newLoan().state()).isEqualTo(Loan.State.REQUESTED);
    }

    @Test
    void approvingRequestedLoanApprovesIt() {
        final Loan loan = Loans.newLoan();
        loan.approve();
        assertThat(loan.state()).isEqualTo(Loan.State.APPROVED);
    }

    @Test
    void repayingApprovedLoanRepaysIt() {
        final Loan loan = Loans.newLoan();
        loan.approve();
        loan.repay();
        assertThat(loan.state()).isEqualTo(Loan.State.REPAID);
    }

    @Test
    void repayingRepaidLoanIsRefused() {
        final Loan loan = Loans.newLoan();
        loan.approve();
        loan.repay();
        assertThatIllegalStateException().isThrownBy(loan::repay);
    }
}
