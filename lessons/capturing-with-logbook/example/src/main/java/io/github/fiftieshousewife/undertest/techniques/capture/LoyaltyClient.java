package io.github.fiftieshousewife.undertest.techniques.capture;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.httpclient5.LogbookHttpExecHandler;
import tools.jackson.databind.json.JsonMapper;

/** Asks the loyalty provider how many points a customer has, by their email address. Logbook records each call. */
public final class LoyaltyClient implements AutoCloseable {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final CloseableHttpClient http;
    private final URI base;
    private final String key;

    public LoyaltyClient(final URI base, final String key, final Logbook logbook) {
        this.http = HttpClients.custom()
                .addExecInterceptorFirst("logbook", new LogbookHttpExecHandler(logbook))
                .build();
        this.base = base;
        this.key = key;
    }

    /** The customer's points, from the provider's JSON answer. */
    public int pointsFor(final String email) {
        final HttpPost post = new HttpPost(base.resolve("/points"));
        post.setHeader("Authorization", "Bearer " + key);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(JSON.createObjectNode().put("email", email).toString(),
                ContentType.APPLICATION_JSON));
        try {
            return http.execute(post, answer -> JSON.readTree(EntityUtils.toString(answer.getEntity())))
                    .path("points").asInt();
        } catch (IOException e) {
            throw new UncheckedIOException("the loyalty provider can't be reached", e);
        }
    }

    @Override
    public void close() throws IOException {
        http.close();
    }
}
