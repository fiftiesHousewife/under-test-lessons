package io.github.fiftieshousewife.undertest.techniques.liquibase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

/* A change the database refuses stops the update, with the database's own reason in one line. */
class FailedChangeTest {

    @Test
    void aRefusedChangeSaysWhy() throws SQLException {
        final JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:broken;DB_CLOSE_DELAY=-1");
        try (Connection connection = h2.getConnection()) {
            assertThatThrownBy(() -> DatabaseChanges.apply(connection, "db/broken.yaml"))
                    .isInstanceOf(SQLException.class).hasMessageStartingWith("a database change failed: ");
        }
    }
}
