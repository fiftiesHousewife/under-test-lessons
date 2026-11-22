package io.github.fiftieshousewife.undertest.techniques.recorded.fixed;

import io.github.fiftieshousewife.undertest.techniques.recorded.RecordingCleaner;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * FIXED: before a recording is saved, drops the password token it was sent with, and swaps each name and email
 * address in the answer for a keyed substitute. The same value always gets the same substitute, so repeats still match.
 */
public final class KeyedRecordingCleaner implements RecordingCleaner {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    /** The fields that hold personal data, and what follows the substitute for each. */
    private static final Map<String, String> PERSONAL_FIELDS = Map.of("name", "", "email", "@example.invalid");

    private final SecretKeySpec key;

    public KeyedRecordingCleaner(final String key) {
        this.key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Override
    public String clean(final String recording) {
        final JsonNode mapping = JSON.readTree(recording);
        if (mapping.at("/request/headers") instanceof ObjectNode headers) {
            headers.remove("Authorization");
        }
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

    /** The start of the value's HMAC-SHA256 under the key: the same for the same value, and can't be reversed. */
    private String keyed(final String value) {
        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8))).substring(0, 8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA256 is missing from this Java", e);
        }
    }
}
