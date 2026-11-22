package io.github.fiftieshousewife.undertest.techniques.cleaning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans every field of a cleaned capture for anything that still looks personal: an email address outside the
 * example domain kept for tests, a run of 8 to 19 digits (a card or account number), or a name from the capture.
 */
public final class PersonalDataDetector {

    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@([\\w.-]+)");
    private static final Pattern LONG_NUMBER = Pattern.compile("\\b\\d{8,19}\\b");
    private static final String TEST_DOMAIN = "example.com";

    private final List<Pattern> knownNames;

    /** A detector that also looks for these names: the customers' names, taken from the capture before cleaning. */
    public PersonalDataDetector(final Collection<String> knownNames) {
        this.knownNames = knownNames.stream().distinct().map(PersonalDataDetector::anyCase).toList();
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
        if (LONG_NUMBER.matcher(value).find()) {
            kinds.add("a card or account number");
        }
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
