package io.github.fiftieshousewife.undertest.techniques.masking;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

/** One captured file, such as a day's orders: its name, its header and its rows, each row a field-to-value map. */
public record CapturedFile(String name, List<String> header, List<Map<String, String>> rows) {

    public CapturedFile {
        header = List.copyOf(header);
        rows = List.copyOf(rows.stream().map(row -> Collections.unmodifiableMap(new LinkedHashMap<>(row))).toList());
    }

    /** Reads a captured CSV file: the first line names the fields, and each line after it is one row. */
    public static CapturedFile parse(final String name, final List<String> lines) {
        final List<String> header = Arrays.asList(lines.getFirst().split(",", -1));
        final List<Map<String, String>> rows = lines.stream().skip(1).map(line -> line.split(",", -1))
                .map(values -> row(header, values)).toList();
        return new CapturedFile(name, header, rows);
    }

    private static Map<String, String> row(final List<String> header, final String... values) {
        final Map<String, String> row = new LinkedHashMap<>();
        IntStream.range(0, header.size()).forEach(field -> row.put(header.get(field), values[field]));
        return row;
    }

    /** The values of one field, in row order. */
    public List<String> column(final String field) {
        return rows.stream().map(row -> row.get(field)).toList();
    }

    /** A copy with each field that has a rule replaced by the rule's result; other fields are kept as they are. */
    public CapturedFile masked(final Map<String, UnaryOperator<String>> rules) {
        final List<Map<String, String>> maskedRows = rows.stream().map(row -> {
            final Map<String, String> masked = new LinkedHashMap<>(row);
            masked.replaceAll((field, value) -> rules.getOrDefault(field, UnaryOperator.identity()).apply(value));
            return masked;
        }).toList();
        return new CapturedFile(name, header, maskedRows);
    }
}
