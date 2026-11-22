package io.github.fiftieshousewife.undertest.techniques.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Keeps the choice honest: code tested only on H2 may use only SQL that every database runs the same way. */
class PortableSqlTest {

    private static final Path MAIN_CODE = Path.of("src", "main");

    @Test
    void theCodeTestedOnH2UsesOnlyPortableSql() throws IOException {
        assertThat(PortableSql.vendorOnlySqlIn(MAIN_CODE)).as("vendor-only SQL in code tested on H2").isEmpty();
    }

    @Test
    void theCheckFindsSqlThatDependsOnTheDatabase() {
        final String postgresOnly = "SELECT name FROM customers WHERE name ILIKE ? ORDER BY last_order NULLS FIRST";

        assertThat(PortableSql.vendorOnly(postgresOnly))
                .as("vendor-only SQL found")
                .containsExactly("ILIKE", "NULLS FIRST");
    }
}
