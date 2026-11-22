package io.github.fiftieshousewife.undertest.techniques.nested.fixed;

import io.github.fiftieshousewife.undertest.techniques.nested.Loan;

/** FIXED: each step is allowed only from the state before it. */
public final class FixedLoan implements Loan {

    private State current = State.REQUESTED;

    @Override
    public State state() {
        return current;
    }

    @Override
    public void approve() {
        move(State.REQUESTED, State.APPROVED, "only a requested loan can be approved");
    }

    @Override
    public void repay() {
        move(State.APPROVED, State.REPAID, "only an approved loan can be repaid");
    }

    private void move(final State from, final State to, final String refusal) {
        if (current != from) {
            throw new IllegalStateException(refusal);
        }
        current = to;
    }
}
