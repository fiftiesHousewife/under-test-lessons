package io.github.fiftieshousewife.undertest.techniques.liquibase;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/* AFTER: the same changes applied to real PostgreSQL, the database production uses, in a throwaway container. */
@Testcontainers(disabledWithoutDocker = true)
class RealPostgresChangesTest {

    @Container
    // The image: postgresImage in gradle.properties, such as a company's own approved build of PostgreSQL.
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(DockerImageName
            .parse(System.getProperty("postgres.image", "postgres:17-alpine")).asCompatibleSubstituteFor("postgres"));

    @Test
    void everyChangeApplies() throws SQLException {
        final String changelog = System.getProperty("code", "fixed").matches("today|answer")
                ? "db/changelog-today.yaml" : "db/changelog.yaml";
        try (Connection connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(),
                POSTGRES.getPassword())) {
            DatabaseChanges.apply(connection, changelog);
            assertThat(connection.getMetaData().getColumns(null, null, "customers", "loyalty_level").next()).isTrue();
        }
    }
}
