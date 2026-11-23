package io.github.fiftieshousewife.undertest.techniques.liquibase.answer;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.fiftieshousewife.undertest.techniques.liquibase.Changelogs;
import java.io.StringWriter;
import java.nio.file.Path;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.OfflineConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * THE EXERCISE'S ANSWER: with no container runtime, Liquibase can still write out the SQL it would run on PostgreSQL,
 * from an offline connection that only names the database. The loyalty level must get a type PostgreSQL has. Run it
 * against today's changelog with {@code -Pcode=answer}: raw SQL goes to PostgreSQL as written, TINYINT and all.
 */
class PostgresSqlAnswerTest {

    @Test
    @SuppressWarnings("deprecation") // as in DatabaseChanges: Liquibase's simplest entry point
    void theLoyaltyLevelGetsATypePostgresHas(@TempDir final Path folder) throws LiquibaseException {
        final ResourceAccessor files = new ClassLoaderResourceAccessor();
        final String offline = "offline:postgresql?changeLogFile=" + folder.resolve("databasechangelog.csv");
        final Database postgres = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new OfflineConnection(offline, files));
        final StringWriter sql = new StringWriter();

        new Liquibase(Changelogs.current(), files, postgres).update("", sql);

        final String loyaltyLevel = sql.toString().lines().filter(line -> line.contains("loyalty_level"))
                .findFirst().orElse("no SQL for the loyalty level");
        assertThat(loyaltyLevel).as("the SQL PostgreSQL would be sent").contains("SMALLINT");
    }
}
