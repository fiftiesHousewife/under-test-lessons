package io.github.fiftieshousewife.undertest.techniques.blackbox;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** The order service's settings, read from a properties file: its port, its database and the warehouse's address. */
public record Settings(int port, String databaseUrl, URI warehouseUrl) {

    /** Reads the settings file the service ships with, or the one a test writes. */
    public static Settings load(final Path file) {
        final Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new Settings(Integer.parseInt(properties.getProperty("port")),
                properties.getProperty("database.url"),
                URI.create(properties.getProperty("warehouse.url")));
    }
}
