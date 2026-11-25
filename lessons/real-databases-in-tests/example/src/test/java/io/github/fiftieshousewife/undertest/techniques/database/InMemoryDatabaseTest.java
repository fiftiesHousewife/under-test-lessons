package io.github.fiftieshousewife.undertest.techniques.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.time.LocalDate;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

/* BEFORE: the repository on H2, a database held in memory. Quick, and not the database production uses. */
class InMemoryDatabaseTest {

    @Test
    void customersWhoNeverOrderedComeFirst() throws SQLException {
        final JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:customers;DB_CLOSE_DELAY=-1");
        final CustomerRepository customers = new CustomerRepository(h2::getConnection, Queries.fixed());
        customers.createTable();
        customers.add("Ada", LocalDate.of(2024, 3, 1));
        customers.add("Bob", null);
        customers.add("Cy", LocalDate.of(2023, 7, 9));

        assertThat(customers.whoToCallFirst()).containsExactly("Bob", "Cy", "Ada");
    }
}
