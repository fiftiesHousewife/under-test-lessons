package io.github.fiftieshousewife.undertest.techniques.liquibase;

import java.sql.Connection;
import java.sql.SQLException;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;

/** Brings a database up to date with a changelog, as the application does when it starts. */
public final class DatabaseChanges {

    private DatabaseChanges() {
    }

    /**
     * Applies every change in the changelog that the database hasn't had yet, with Liquibase's update command, which
     * reads the changelog from the class path.
     */
    public static void apply(final Connection connection, final String changelog) throws SQLException {
        try {
            final Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            new CommandScope(UpdateCommandStep.COMMAND_NAME)
                    .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                    .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changelog)
                    .execute();
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
