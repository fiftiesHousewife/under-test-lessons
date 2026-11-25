package io.github.fiftieshousewife.undertest.techniques.characterisation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/*
 * BEFORE: the kind of test legacy code often has. It runs every line, but checks only that something came back, so a
 * change to any amount, fee or balance would pass unnoticed.
 */
class StatementWeakTest {

    @Test
    void printsSomething() {
        final String statement = new LegacyStatement().print("J. Smith", List.of(
                new Transaction("01 Mar", "Salary", 1200), new Transaction("02 Mar", "Rent", -1200)));

        assertAll(
                () -> assertNotNull(statement),
                () -> assertTrue(statement.contains("Closing balance")));
    }
}
