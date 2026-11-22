package io.github.fiftieshousewife.undertest.techniques.liquibase;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** AFTER: the same changes applied to real PostgreSQL, the database production uses, in a throwaway container. */
@Testcontainers(disabledWithoutDocker = true)
class RealPostgresChangesTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @Test
    void everyChangeApplies() throws SQLException {
        final String changelog = "today".equals(System.getProperty("code"))
                ? "db/changelog-today.yaml" : "db/changelog.yaml";
        try (Connection connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(),
                POSTGRES.getPassword())) {
            DatabaseChanges.apply(connection, changelog);
            assertThat(connection.getMetaData().getColumns(null, null, "customers", "loyalty_level").next()).isTrue();
        }
    }
}
