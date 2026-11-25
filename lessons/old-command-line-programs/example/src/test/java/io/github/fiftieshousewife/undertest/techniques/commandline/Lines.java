package io.github.fiftieshousewife.undertest.techniques.commandline;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/* Standard input for a test: the lines of a payments file. */
final class Lines {

    private Lines() {
    }

    static InputStream of(final String... lines) {
        return new ByteArrayInputStream((String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8));
    }
}
