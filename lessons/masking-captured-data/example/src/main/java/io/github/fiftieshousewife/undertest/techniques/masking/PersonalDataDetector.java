package io.github.fiftieshousewife.undertest.techniques.masking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans every field of masked data for anything that still has a known personal shape: an email address outside
 * the domain kept for tests, a card or account number, an IBAN, a sort code, a US or UK phone number, a US Social
 * Security number, a ZIP+4 code, a UK postcode, a National Insurance number, or a name from the real data. It finds
 * known shapes only: data it passes still needs its masking rules reviewed, and is still treated as personal data.
 */
public final class PersonalDataDetector {

    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@([\\w.-]+)");
    private static final String TEST_DOMAIN = "example.invalid";
    private static final Map<String, Pattern> SHAPES = personalNumbers();

    private final List<Pattern> knownNames;

    /** A detector that also looks for these names: the customers' names, taken from the capture before masking. */
    public PersonalDataDetector(final Collection<String> knownNames) {
        this.knownNames = knownNames.stream().distinct().map(PersonalDataDetector::anyCase).toList();
    }

    /*
     * Each kind of personal number and its shape. Digits next to letters count, as an IBAN's follow its bank code, but
     * a number marked {@code TEST}, as made-up account numbers are, is allowed, as the test email domain is.
     */
    private static Map<String, Pattern> personalNumbers() {
        final Map<String, Pattern> shapes = new LinkedHashMap<>();
        shapes.put("a card or account number", Pattern.compile("(?<!\\d)(?<!TEST)\\d{8,19}(?!\\d)"));
        shapes.put("an IBAN", Pattern.compile("\\b[A-Z]{2}\\d{2}[A-Z0-9]{11,30}\\b"));
        shapes.put("a sort code", Pattern.compile("(?<!\\d)\\d{2}-\\d{2}-\\d{2}(?![\\d-])"));
        shapes.put("a phone number",
                Pattern.compile("(?<![\\w+])(?:\\+44 ?|0)\\d{2,4}[ -]?\\d{3,4}[ -]?\\d{3,4}(?!\\d)"));
        shapes.put("a US phone number", Pattern.compile("(?<![\\w+(])(?:\\+1 )?\\(?\\d{3}\\)? ?\\d{3}-\\d{4}(?!\\d)"));
        shapes.put("a Social Security number", Pattern.compile("(?<![\\d-])\\d{3}-\\d{2}-\\d{4}(?![\\d-])"));
        shapes.put("a ZIP code", Pattern.compile("(?<![\\d-])\\d{5}-\\d{4}(?![\\d-])"));
        shapes.put("a postcode", Pattern.compile("\\b[A-Z]{1,2}\\d[A-Z\\d]? ?\\d[A-Z]{2}\\b"));
        shapes.put("a National Insurance number",
                Pattern.compile("\\b[A-CEGHJ-PR-TW-Z]{2} ?\\d{2} ?\\d{2} ?\\d{2} ?[A-D]\\b"));
        return shapes;
    }

    private static Pattern anyCase(final String name) {
        return Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    /** One finding per field that still looks personal, such as "orders.csv row 4, note: an email address". */
    public List<String> findings(final Capture capture) {
        final List<String> findings = new ArrayList<>();
        for (final CapturedFile file : capture.files()) {
            for (int row = 0; row < file.rows().size(); row++) {
                for (final Map.Entry<String, String> field : file.rows().get(row).entrySet()) {
                    final String where = "%s row %d, %s: ".formatted(file.name(), row + 1, field.getKey());
                    looksPersonal(field.getValue()).forEach(kind -> findings.add(where + kind));
                }
            }
        }
        return findings;
    }

    private List<String> looksPersonal(final String value) {
        final List<String> kinds = new ArrayList<>();
        if (hasRealEmail(value)) {
            kinds.add("an email address");
        }
        SHAPES.forEach((kind, shape) -> {
            if (shape.matcher(value).find()) {
                kinds.add(kind);
            }
        });
        if (knownNames.stream().anyMatch(name -> name.matcher(value).find())) {
            kinds.add("a customer's name");
        }
        return kinds;
    }

    private static boolean hasRealEmail(final String value) {
        final Matcher email = EMAIL.matcher(value);
        while (email.find()) {
            if (!TEST_DOMAIN.equals(email.group(1))) {
                return true;
            }
        }
        return false;
    }
}
