package io.github.fiftieshousewife.undertest.techniques.recorded.fixed;

import io.github.fiftieshousewife.undertest.techniques.recorded.RecordingCleaner;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * FIXED: before a recording is saved, drops every credential it holds, in both directions: password tokens, cookies,
 * API keys, and tokens in the address. Then it swaps each name and email address in the answer for a keyed
 * substitute. The same value always gets the same substitute, so repeats still match.
 */
public final class KeyedRecordingCleaner implements RecordingCleaner {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    /** The fields that hold personal data, and what follows the substitute for each. */
    private static final Map<String, String> PERSONAL_FIELDS = Map.of("name", "", "email", "@example.invalid");
    /** Headers that carry a password, a session or a key, in either direction. */
    private static final List<String> CREDENTIALS =
            List.of("authorization", "proxy-authorization", "cookie", "set-cookie", "x-api-key");
    /** Query parameters that carry a token or a key, in the recorded address. */
    private static final Pattern TOKEN_IN_ADDRESS = Pattern.compile(
            "[?&](?:access_token|token|api_key|apikey|key|sig|signature)=[^&]*", Pattern.CASE_INSENSITIVE);
    private static final int SUBSTITUTE_HEX_DIGITS = 16;

    private final SecretKeySpec key;

    public KeyedRecordingCleaner(final String key) {
        this.key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Override
    public String clean(final String recording) {
        final JsonNode mapping = JSON.readTree(recording);
        removeCredentials(mapping.at("/request/headers"));
        removeCredentials(mapping.at("/response/headers"));
        removeTokens(mapping.path("request"));
        final ObjectNode response = (ObjectNode) mapping.get("response");
        final JsonNode body = JSON.readTree(response.path("body").asString());
        replacePersonalData(body);
        response.put("body", JSON.writeValueAsString(body));
        return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(mapping);
    }

    private void replacePersonalData(final JsonNode node) {
        if (node instanceof ObjectNode object) {
            PERSONAL_FIELDS.forEach((field, ending) -> {
                if (object.path(field).isString()) {
                    object.put(field, "person-" + keyed(object.get(field).asString()) + ending);
                }
            });
        }
        node.forEach(this::replacePersonalData);
    }

    private static void removeCredentials(final JsonNode headers) {
        if (headers instanceof ObjectNode named) {
            named.propertyNames().stream().filter(name -> CREDENTIALS.contains(name.toLowerCase(Locale.ROOT)))
                    .toList().forEach(named::remove);
        }
    }

    /** Removes tokens from the recorded address, written as a whole url or as separate query parameters. */
    private static void removeTokens(final JsonNode request) {
        if (request instanceof ObjectNode asked) {
            if (asked.path("url").isString()) {
                asked.put("url", TOKEN_IN_ADDRESS.matcher(asked.get("url").asString()).replaceAll("")
                        .replaceFirst("^([^?]*)&", "$1?"));
            }
            if (asked.path("queryParameters") instanceof ObjectNode parameters) {
                parameters.propertyNames().stream()
                        .filter(name -> TOKEN_IN_ADDRESS.matcher("?" + name + "=").find())
                        .toList().forEach(parameters::remove);
            }
        }
    }

    /** The first 64 bits of the value's HMAC-SHA256 under the key: the same for the same value, never reversed. */
    private String keyed(final String value) {
        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)))
                    .substring(0, SUBSTITUTE_HEX_DIGITS);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA256 is missing from this Java", e);
        }
    }
}
