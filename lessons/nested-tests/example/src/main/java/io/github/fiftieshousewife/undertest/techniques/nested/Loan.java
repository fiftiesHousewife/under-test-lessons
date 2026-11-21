package io.github.fiftieshousewife.undertest.techniques.nested;

/** A loan: requested, then approved, then repaid. Each step is allowed only from the one before. */
public interface Loan {

    enum State { REQUESTED, APPROVED, REPAID }

    State state();

    void approve();

    void repay();
}
