package io.github.fiftieshousewife.undertest.techniques.nested;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/* AFTER: one nested class per situation, each with the same two questions: can it be approved, can it be repaid? */
@DisplayName("A loan")
class LoanTest {

    private final Loan loan = Loans.newLoan();

    @Nested
    @DisplayName("when requested")
    class WhenRequested {

        @Test
        @DisplayName("can be approved")
        void canBeApproved() {
            loan.approve();
            assertThat(loan.state()).isEqualTo(Loan.State.APPROVED);
        }

        @Test
        @DisplayName("cannot be repaid")
        void cannotBeRepaid() {
            assertThatIllegalStateException().as("repaying a loan that was never approved").isThrownBy(loan::repay);
        }
    }

    @Nested
    @DisplayName("when approved")
    class WhenApproved {

        @BeforeEach
        void approve() {
            loan.approve();
        }

        @Test
        @DisplayName("can be repaid")
        void canBeRepaid() {
            loan.repay();
            assertThat(loan.state()).isEqualTo(Loan.State.REPAID);
        }

        @Test
        @DisplayName("cannot be approved again")
        void cannotBeApprovedAgain() {
            assertThatIllegalStateException().as("approving a loan a second time").isThrownBy(loan::approve);
        }
    }

    @Nested
    @DisplayName("when repaid")
    class WhenRepaid {

        @BeforeEach
        void approveAndRepay() {
            loan.approve();
            loan.repay();
        }

        @Test
        @DisplayName("cannot be approved")
        void cannotBeApproved() {
            assertThatIllegalStateException().as("approving a loan already repaid").isThrownBy(loan::approve);
        }

        @Test
        @DisplayName("cannot be repaid again")
        void cannotBeRepaidAgain() {
            assertThatIllegalStateException().as("repaying a loan a second time").isThrownBy(loan::repay);
        }
    }
}
