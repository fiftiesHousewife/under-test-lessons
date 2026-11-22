package io.github.fiftieshousewife.undertest.techniques.liquibase;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

/** BEFORE: the changes applied to H2, the in-memory database the developers use. */
class InMemoryChangesTest {

    @Test
    void everyChangeApplies() throws SQLException {
        final JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:changes;DB_CLOSE_DELAY=-1");
        try (Connection connection = h2.getConnection()) {
            DatabaseChanges.apply(connection, Changelogs.current());
            assertThat(connection.getMetaData().getColumns(null, null, "CUSTOMERS", "LOYALTY_LEVEL").next()).isTrue();
        }
    }
}
