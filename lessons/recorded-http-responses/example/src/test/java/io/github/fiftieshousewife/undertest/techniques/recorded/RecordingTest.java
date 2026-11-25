package io.github.fiftieshousewife.undertest.techniques.recorded;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.recordSpec;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

/**
 * BEFORE and AFTER for making recordings: WireMock records a stand-in supplier running on this machine. Saved as
 * recorded, the files hold a person's name, email address and password token. Masked first, they hold none of them.
 */
class RecordingTest {

    private static final String NAME = "Ada Lovelace";
    private static final String EMAIL = "ada.lovelace@example.com";
    private static final String TOKEN = "Bearer supplier-token-4f7q";
    private static final String ACCOUNT = "\"requestedBy\": {\"name\": \"" + NAME + "\", \"email\": \"" + EMAIL + "\"}";

    @TempDir
    private Path saved;
    private WireMockServer standIn;
    private WireMockServer recorder;

    @BeforeEach
    void start() {
        standIn = new WireMockServer(options().dynamicPort().usingFilesUnderDirectory(saved.toString()));
        recorder = new WireMockServer(options().dynamicPort().usingFilesUnderDirectory(saved.toString()));
        standIn.start();
        recorder.start();
        standIn.stubFor(get("/postcodes/LS1%204AP")
                .willReturn(okJson("{\"town\": \"Leeds\", " + ACCOUNT + "}")));
        standIn.stubFor(get("/postcodes/ZZ9%209ZZ").willReturn(notFound()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"unknown postcode\", " + ACCOUNT + "}")));
    }

    @AfterEach
    void stop() {
        recorder.stop();
        standIn.stop();
    }

    @Test
    void savedAsRecordedTheFilesHoldPersonalData() throws IOException, InterruptedException {
        final List<String> files = recordAndSave(recording -> recording);
        assertThat(personalDataIn(files)).as("personal data in the saved recordings")
                .containsExactly(NAME, EMAIL, TOKEN);
    }

    @Test
    void maskedFirstTheFilesHoldSubstitutes() throws IOException, InterruptedException {
        final List<String> files = recordAndSave(Clients.cleaner());
        assertAll(
                () -> assertThat(personalDataIn(files)).as("personal data in the saved recordings").isEmpty(),
                () -> assertThat(namesIn(files)).as("names in the saved recordings")
                        .isEqualTo(List.of("person-ea08c4400378ace1", "person-ea08c4400378ace1")));
    }

    /** Records two questions to the stand-in, then masks each recording and saves it to a file. */
    private List<String> recordAndSave(final RecordingCleaner cleaner) throws IOException, InterruptedException {
        recorder.startRecording(recordSpec().forTarget(standIn.baseUrl())
                .captureHeader("Authorization").makeStubsPersistent(false));
        ask("/postcodes/LS1%204AP");
        ask("/postcodes/ZZ9%209ZZ");
        final List<String> files = new ArrayList<>();
        for (final StubMapping recording : recorder.stopRecording().getStubMappings()) {
            final Path file = saved.resolve(recording.getName() + ".json");
            Files.writeString(file, cleaner.mask(Json.write(recording)));
            files.add(Files.readString(file));
        }
        return files;
    }

    private void ask(final String path) throws IOException, InterruptedException {
        final HttpRequest question = HttpRequest.newBuilder(URI.create(recorder.baseUrl() + path))
                .header("Authorization", TOKEN).build();
        HttpClient.newHttpClient().send(question, HttpResponse.BodyHandlers.discarding());
    }

    /** Which of the person's name, email address and token appear anywhere in the saved files. */
    private static List<String> personalDataIn(final List<String> files) {
        return Stream.of(NAME, EMAIL, TOKEN).filter(value -> files.stream().anyMatch(file -> file.contains(value)))
                .toList();
    }

    /** The name in each saved answer, in the order the questions were asked. */
    private static List<String> namesIn(final List<String> files) {
        final JsonMapper json = JsonMapper.builder().build();
        return files.stream()
                .map(file -> json.readTree(json.readTree(file).at("/response/body").asString()))
                .map(body -> body.at("/requestedBy/name").asString())
                .toList();
    }
}
