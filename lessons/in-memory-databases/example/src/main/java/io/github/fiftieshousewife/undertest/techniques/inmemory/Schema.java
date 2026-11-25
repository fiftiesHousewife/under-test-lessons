package io.github.fiftieshousewife.undertest.techniques.inmemory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** The database's tables, from the one schema file the application applies to production, and tests apply too. */
public final class Schema {

    /** The schema file, on the classpath. */
    public static final String FILE = "/db/schema.sql";

    private Schema() {
    }

    /* Creates every table and index in the schema file. */
    @SuppressFBWarnings(value = {"SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE", "SQL_INJECTION_JDBC"},
            justification = "The statements come from the application's own schema file, never from users")
    public static void apply(final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (final String sql : statements()) {
                statement.execute(sql);
            }
        }
    }

    /** The schema file's statements, without its comments. */
    public static List<String> statements() {
        final String withoutComments = read().lines()
                .filter(line -> !line.strip().startsWith("--"))
                .collect(Collectors.joining("\n"));
        return Arrays.stream(withoutComments.split(";"))
                .map(String::strip)
                .filter(sql -> !sql.isEmpty())
                .toList();
    }

    private static String read() {
        try (InputStream file = Schema.class.getResourceAsStream(FILE)) {
            if (file == null) {
                throw new IllegalStateException("no schema file on the classpath: " + FILE);
            }
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
