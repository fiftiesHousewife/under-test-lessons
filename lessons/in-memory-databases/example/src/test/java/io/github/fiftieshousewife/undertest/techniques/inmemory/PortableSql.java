package io.github.fiftieshousewife.undertest.techniques.inmemory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/* Finds SQL that only one database runs, or that depends on the database's own behaviour. */
final class PortableSql {

    private static final Pattern VENDOR_ONLY =
            // PostgreSQL's own SQL, and SQL whose answer depends on the database: the order of nulls, the time of day.
            Pattern.compile(
            "\\b(ILIKE|NULLS FIRST|NULLS LAST|ON CONFLICT|RETURNING|DISTINCT ON|JSONB|SERIAL)\\b|\\bNOW\\(\\)|::",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern JAVA_TEXT = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"");

    private PortableSql() {
    }

    /* Each vendor-only piece of SQL in the folder's SQL files and in its Java code's text, as "file: SQL". */
    static List<String> vendorOnlySqlIn(final Path folder) throws IOException {
        final List<String> found = new ArrayList<>();
        try (Stream<Path> files = Files.walk(folder)) {
            for (final Path file : files.filter(Files::isRegularFile).toList()) {
                for (final String sql : vendorOnly(sqlIn(file))) {
                    found.add(file.getFileName() + ": " + sql);
                }
            }
        }
        return found;
    }

    /* The vendor-only pieces of one piece of SQL. */
    static List<String> vendorOnly(final String sql) {
        final List<String> found = new ArrayList<>();
        final Matcher matcher = VENDOR_ONLY.matcher(sql);
        while (matcher.find()) {
            found.add(matcher.group().toUpperCase(Locale.ROOT));
        }
        return found;
    }

    private static String sqlIn(final Path file) throws IOException {
        final String text = Files.readString(file, StandardCharsets.UTF_8);
        if (file.toString().endsWith(".sql")) {
            return text;
        }
        if (!file.toString().endsWith(".java")) {
            return "";
        }
        final StringBuilder strings = new StringBuilder();
        final Matcher matcher = JAVA_TEXT.matcher(text);
        while (matcher.find()) {
            strings.append(matcher.group()).append('\n');
        }
        return strings.toString();
    }
}
