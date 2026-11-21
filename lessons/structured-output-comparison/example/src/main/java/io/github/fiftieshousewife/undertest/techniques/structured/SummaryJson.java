package io.github.fiftieshousewife.undertest.techniques.structured;

import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import tools.jackson.databind.json.JsonMapper;

/** Writes a summary as JSON, with a new id and the time it was made: both differ on every run. */
public final class SummaryJson {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final Clock clock;

    public SummaryJson(final Clock clock) {
        this.clock = clock;
    }

    public String write(final List<Line> lines, final int totalPence) {
        final Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("id", UUID.randomUUID().toString());
        summary.put("generatedAt", clock.instant().toString());
        summary.put("lines", lines);
        summary.put("totalPence", totalPence);
        return JSON.writeValueAsString(summary);
    }
}
