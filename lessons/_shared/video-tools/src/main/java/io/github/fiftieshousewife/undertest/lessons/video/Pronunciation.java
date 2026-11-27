package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * How the voice should say words it would otherwise get wrong, such as "PIT" read as letters. Applied only to the
 * spoken narration; slides keep the written form. The file has one {@code word = how to say it} per line, and lines
 * starting with {@code #} are comments. A word may be a phrase, and may start or end with a sign, as in C++. All the
 * words are replaced in one pass, the longest first where two could match at the same place, so a phrase wins over a
 * word inside it, and what one rule puts in is never changed by another.
 */
final class Pronunciation {

    private static final Pattern MONEY = Pattern.compile("£(\\d{1,3}(?:,\\d{3})+|\\d+)(?:\\.(\\d{2}))?");

    private final Map<String, String> spoken;
    private final Pattern words;

    private Pronunciation(final Map<String, String> spoken) {
        this.spoken = spoken;
        this.words = spoken.isEmpty() ? null : Pattern.compile("(?<![\\w])(?:" + spoken.keySet().stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .map(Pattern::quote)
                .collect(Collectors.joining("|")) + ")(?![\\w])");
    }

    /* Reads the pronunciation file, or returns one that changes nothing if there is no file. */
    static Pronunciation read(final Path file) {
        final Map<String, String> spoken = new LinkedHashMap<>();
        if (Files.isRegularFile(file)) {
            for (final String line : lines(file)) {
                final int equals = line.indexOf('=');
                if (isEntry(line)) {
                    spoken.put(line.substring(0, equals).strip(), line.substring(equals + 1).strip());
                }
            }
        }
        return new Pronunciation(spoken);
    }

    /* A line that says how to say a word ({@code word = how}), not a blank line or a comment. */
    private static boolean isEntry(final String line) {
        return !line.isBlank() && !line.strip().startsWith("#") && line.indexOf('=') > 0;
    }

    /*
     * The text with each listed word (whole words only, matching case) replaced by how to say it, and sums of money
     * said as a person says them: "£12.50" as "12 pounds 50", "£1" as "1 pound".
     */
    String forSpeech(final String text) {
        final String money = MONEY.matcher(text).replaceAll(Pronunciation::pounds);
        if (words == null) {
            return money;
        }
        return words.matcher(money).replaceAll(found -> Matcher.quoteReplacement(spoken.get(found.group())));
    }

    /* Each listed word or phrase the text uses, once, in the order it first appears, with how to say it. */
    Map<String, String> used(final String text) {
        final Map<String, String> found = new LinkedHashMap<>();
        if (words != null) {
            final Matcher match = words.matcher(text);
            while (match.find()) {
                found.putIfAbsent(match.group(), spoken.get(match.group()));
            }
        }
        return found;
    }

    /* "£12.50" as "12 pounds 50", "£1.00" as "1 pound", "£0.50" as "50 pence". */
    private static String pounds(final MatchResult money) {
        final String whole = money.group(1).replace(",", "");
        final String pence = money.group(2);
        if ("0".equals(whole) && pence != null) {
            return Integer.parseInt(pence) + " pence";
        }
        final String unit = "1".equals(whole) ? "pound" : "pounds";
        return whole + " " + unit + andPence(pence);
    }

    private static String andPence(final String pence) {
        return pence == null || "00".equals(pence) ? "" : " " + pence;
    }

    private static java.util.List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
