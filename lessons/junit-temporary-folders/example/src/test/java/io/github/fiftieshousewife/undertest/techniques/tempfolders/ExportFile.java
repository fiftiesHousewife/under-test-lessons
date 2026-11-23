package io.github.fiftieshousewife.undertest.techniques.tempfolders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Reads the orders.csv a test's export wrote, failing in one line, with no path, if there is none. */
public final class ExportFile {

    private ExportFile() {
    }

    public static List<String> linesIn(final Path folder) throws IOException {
        final Path file = folder.resolve(OrdersExport.FILE_NAME);
        if (!Files.exists(file)) {
            throw new AssertionError("No " + OrdersExport.FILE_NAME + " was written");
        }
        return Files.readAllLines(file);
    }
}
