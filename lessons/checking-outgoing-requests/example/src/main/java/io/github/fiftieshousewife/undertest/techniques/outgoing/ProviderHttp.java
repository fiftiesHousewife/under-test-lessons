package io.github.fiftieshousewife.undertest.techniques.outgoing;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/** Sends requests to the payment provider's refunds address, and reads the refund id from its JSON answer. */
public final class ProviderHttp {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final HttpClient http = HttpClient.newHttpClient();
    private final URI base;
    private final String apiKey;

    public ProviderHttp(final URI base, final String apiKey) {
        this.base = base;
        this.apiKey = apiKey;
    }

    /** A request to the refunds address, signed with the shop's key. */
    public HttpRequest.Builder refundRequest() {
        return HttpRequest.newBuilder(base.resolve("/refunds")).header("Authorization", "Bearer " + apiKey);
    }

    /** An empty JSON object to fill in as a request's body. */
    public static ObjectNode newBody() {
        return JSON.createObjectNode();
    }

    /** Sends the request, and returns the refund id from the provider's answer: 201 Created, or an error. */
    public String send(final HttpRequest request) {
        final HttpResponse<String> answer = exchange(request);
        if (answer.statusCode() != 201) {
            throw new ProviderUnavailableException("the payment provider answered " + answer.statusCode());
        }
        return JSON.readTree(answer.body()).path("refundId").asString();
    }

    private HttpResponse<String> exchange(final HttpRequest request) {
        try {
            return http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new ProviderUnavailableException("the payment provider can't be reached");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProviderUnavailableException("interrupted while asking the payment provider");
        }
    }
}
