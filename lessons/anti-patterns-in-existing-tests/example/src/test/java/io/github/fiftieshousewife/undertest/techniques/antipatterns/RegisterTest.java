package io.github.fiftieshousewife.undertest.techniques.antipatterns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

/** The register: rows read with their decisions, new findings refused, and rows for fixed tests noticed. */
class RegisterTest {

    private final Register register = Register.of(List.of(
            "# kind  test  decision",
            "sleeps   StockTest.waits   fix with the flaky tests",
            "disabled  RefundsTest.old"));
    private final Finding waits = new Finding("sleeps", "StockTest.waits", "StockTest.java:12");
    private final Finding fresh = new Finding("sleeps", "StockTest.sleepsToo", "StockTest.java:18");

    @Test
    void refusesOnlyFindingsItDoesNotHold() {
        assertAll(
                () -> assertThat(register.unrecorded(List.of(waits, fresh))).containsExactly(fresh),
                () -> assertThat(register.fixed(List.of(waits, fresh))).containsExactly("disabled RefundsTest.old"),
                () -> assertThat(Register.empty().unrecorded(List.of(waits))).containsExactly(waits));
    }

    @Test
    void reportsEachFindingWithItsDecision() {
        assertThat(register.report(List.of(waits, fresh)))
                .extracting(line -> String.join("|", line.strip().split(" {2,}")))
                .containsExactly("kind|test|where|decision",
                        "sleeps|StockTest.waits|StockTest.java:12|fix with the flaky tests",
                        "sleeps|StockTest.sleepsToo|StockTest.java:18|not in the register");
    }
}
