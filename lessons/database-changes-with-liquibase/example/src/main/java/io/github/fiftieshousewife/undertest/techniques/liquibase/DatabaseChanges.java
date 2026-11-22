package io.github.fiftieshousewife.undertest.techniques.liquibase;

import java.sql.Connection;
import java.sql.SQLException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

/** Brings a database up to date with a changelog, as the application does when it starts. */
public final class DatabaseChanges {

    private DatabaseChanges() {
    }

    /** Applies every change in the changelog that the database hasn't had yet. */
    @SuppressWarnings("deprecation") // Liquibase 4's simplest entry point; its replacement needs more set-up
    public static void apply(final Connection connection, final String changelog) throws SQLException {
        try {
            final Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            new Liquibase(changelog, new ClassLoaderResourceAccessor(), database).update("");
        } catch (LiquibaseException e) {
            throw new SQLException("a database change failed: " + rootMessage(e), e);
        }
    }

    private static String rootMessage(final Throwable error) {
        Throwable cause = error;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage().lines().findFirst().orElse("");
    }
}
