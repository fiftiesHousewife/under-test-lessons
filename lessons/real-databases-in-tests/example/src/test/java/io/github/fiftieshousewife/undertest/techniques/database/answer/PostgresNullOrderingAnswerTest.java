package io.github.fiftieshousewife.undertest.techniques.database.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.database.CustomerRepository;
import io.github.fiftieshousewife.undertest.techniques.database.Queries;
import java.sql.SQLException;
import java.time.LocalDate;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

/*
 * THE EXERCISE'S ANSWER: where no container runtime is to hand, H2 can be told to sort nulls as PostgreSQL does, last
 * in ascending order. Then the in-memory test catches today's query too. It is still not PostgreSQL: it copies one
 * difference that is known, and the real database is the only way to find the ones that aren't.
 */
class PostgresNullOrderingAnswerTest {

    @Test
    void customersWhoNeverOrderedComeFirstWhenNullsSortAsOnPostgres() throws SQLException {
        final JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:nulls-last;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1");
        final CustomerRepository customers = new CustomerRepository(h2::getConnection, Queries.fixed());
        customers.createTable();
        customers.add("Dee", LocalDate.of(2022, 1, 15));
        customers.add("Eve", null);

        assertThat(customers.whoToCallFirst()).as("who sales should call first").first().isEqualTo("Eve");
    }
}
