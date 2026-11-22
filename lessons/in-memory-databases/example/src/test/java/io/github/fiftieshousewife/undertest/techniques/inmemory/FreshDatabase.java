package io.github.fiftieshousewife.undertest.techniques.inmemory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.h2.jdbcx.JdbcDataSource;

/** A new H2 database, held in memory, with the real schema. It disappears when the test closes it. */
final class FreshDatabase implements AutoCloseable {

    // As close to PostgreSQL as H2 comes: the settings H2's own documentation gives for its PostgreSQL mode.
    private static final String LIKE_POSTGRESQL = ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
    private static final AtomicInteger DATABASES = new AtomicInteger();

    private final JdbcDataSource database = new JdbcDataSource();
    private final Connection keepAlive;

    private FreshDatabase() throws SQLException {
        database.setURL("jdbc:h2:mem:invoices-" + DATABASES.incrementAndGet() + LIKE_POSTGRESQL);
        keepAlive = database.getConnection(); // a named in-memory database lives while a connection to it is open
        Schema.apply(keepAlive); // the same schema file the application applies to production
    }

    static FreshDatabase start() throws SQLException {
        return new FreshDatabase();
    }

    Connections connections() {
        return database::getConnection;
    }

    @Override
    public void close() throws SQLException {
        keepAlive.close(); // the last connection closes, and the database and its rows are gone
    }
}
