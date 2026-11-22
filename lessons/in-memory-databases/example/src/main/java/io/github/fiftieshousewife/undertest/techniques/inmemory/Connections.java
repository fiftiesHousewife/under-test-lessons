package io.github.fiftieshousewife.undertest.techniques.inmemory;

import java.sql.Connection;
import java.sql.SQLException;

/** Where connections come from: the database's own data source, in production and in tests. */
@FunctionalInterface
public interface Connections {
    Connection open() throws SQLException;
}
