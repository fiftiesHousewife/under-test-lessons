package io.github.fiftieshousewife.undertest.techniques.recorded;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * No credential is saved, in either direction: password tokens, cookies, API keys, or tokens in the address. Checked
 * on a recording that holds every kind, and on every recording kept with the tests.
 */
class CredentialsTest {

    private static final Pattern CREDENTIAL = Pattern.compile(
            "(?i)\"(?:authorization|proxy-authorization|cookie|set-cookie|x-api-key)\"|[?&](?:token|api_key|key)=");
    private static final String EVERY_KIND = """
            {"request": {"url": "/postcodes/LS1%204AP?token=t-91&format=json&api_key=k-12", "method": "GET",
              "headers": {"Authorization": {"equalTo": "Bearer supplier-token-4f7q"},
                          "Cookie": {"equalTo": "session=s-55"}, "X-Api-Key": {"equalTo": "k-12"}}},
             "response": {"status": 200, "body": "{\\"town\\": \\"Leeds\\"}",
              "headers": {"Content-Type": "application/json", "Set-Cookie": "session=s-56; HttpOnly"}}}
            """;

    @Test
    void theCleanerDropsEveryCredentialInBothDirections() {
        final String cleaned = Clients.cleaner().clean(EVERY_KIND);
        assertAll(
                () -> assertThat(credentialsIn(cleaned)).as("credentials left in the recording").isEmpty(),
                () -> assertThat(cleaned).as("what the test needs is kept")
                        .contains("format=json", "Content-Type"));
    }

    @Test
    void noRecordingKeptWithTheTestsHoldsACredential() throws IOException, URISyntaxException {
        final Path mappings = Path.of(CredentialsTest.class.getResource("/supplier/mappings").toURI());
        try (Stream<Path> files = Files.list(mappings)) {
            final List<String> withCredentials = files.filter(file -> !credentialsIn(read(file)).isEmpty())
                    .map(file -> String.valueOf(file.getFileName())).toList();
            assertThat(withCredentials).as("recordings holding a credential").isEmpty();
        }
    }

    private static List<String> credentialsIn(final String recording) {
        return CREDENTIAL.matcher(recording).results().map(found -> found.group()).toList();
    }

    private static String read(final Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }
}
