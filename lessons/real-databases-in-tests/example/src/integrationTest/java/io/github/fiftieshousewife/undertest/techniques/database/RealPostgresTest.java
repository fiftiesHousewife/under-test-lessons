package io.github.fiftieshousewife.undertest.techniques.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * AFTER: the same test on real PostgreSQL, the database production uses, started in a container for this test and
 * thrown away afterwards. Skipped where there is no container runtime.
 */
@Testcontainers(disabledWithoutDocker = true)
class RealPostgresTest {

    @Container
    // The image: postgresImage in gradle.properties, such as a company's own approved build of PostgreSQL.
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(DockerImageName
            .parse(System.getProperty("postgres.image", "postgres:17-alpine")).asCompatibleSubstituteFor("postgres"));

    @Test
    void customersWhoNeverOrderedComeFirst() throws SQLException {
        final PGSimpleDataSource postgres = new PGSimpleDataSource();
        postgres.setUrl(POSTGRES.getJdbcUrl());
        postgres.setUser(POSTGRES.getUsername());
        postgres.setPassword(POSTGRES.getPassword());
        final CustomerRepository customers = new CustomerRepository(postgres::getConnection,
                !System.getProperty("code", "fixed").matches("today|answer"));
        customers.createTable();
        customers.add("Ada", LocalDate.of(2024, 3, 1));
        customers.add("Bob", null);
        customers.add("Cy", LocalDate.of(2023, 7, 9));

        assertThat(customers.whoToCallFirst()).containsExactly("Bob", "Cy", "Ada");
    }
}
