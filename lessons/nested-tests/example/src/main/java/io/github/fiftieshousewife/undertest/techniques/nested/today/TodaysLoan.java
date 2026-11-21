package io.github.fiftieshousewife.undertest.techniques.nested.today;

import io.github.fiftieshousewife.undertest.techniques.nested.Loan;

/** TODAY: refuses to repay a loan that is already repaid, but not one that was never approved. */
public final class TodaysLoan implements Loan {

    private State state = State.REQUESTED;

    @Override
    public State state() {
        return state;
    }

    @Override
    public void approve() {
        if (state != State.REQUESTED) {
            throw new IllegalStateException("only a requested loan can be approved");
        }
        state = State.APPROVED;
    }

    @Override
    public void repay() {
        if (state == State.REPAID) {
            throw new IllegalStateException("only an approved loan can be repaid");
        }
        state = State.REPAID;
    }
}
