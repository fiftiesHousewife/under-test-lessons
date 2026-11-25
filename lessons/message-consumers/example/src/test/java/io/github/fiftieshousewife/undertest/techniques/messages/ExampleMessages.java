package io.github.fiftieshousewife.undertest.techniques.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/* The example messages: collected by browsing a real queue, masked, and kept one per line, after comment lines. */
final class ExampleMessages {

    private ExampleMessages() {
    }

    static List<String> payments() {
        try (InputStream in = Objects.requireNonNull(
                ExampleMessages.class.getResourceAsStream("/example-messages/payments.txt"), "example messages")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8).lines()
                    .filter(line -> !line.isBlank() && !line.startsWith("#"))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static String forOrder(final String order) {
        return payments().stream().filter(text -> text.contains(";order=" + order + ";")).findFirst().orElseThrow();
    }
}
