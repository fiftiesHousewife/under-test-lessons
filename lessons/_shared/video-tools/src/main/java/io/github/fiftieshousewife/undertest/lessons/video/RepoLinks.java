package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Fills in the addresses of the lessons and toolkit repositories on slides, from the system properties
 * {@code video.lessonsRepo} and {@code video.toolkitRepo} (the build's lessonsRepoUrl and toolkitRepoUrl). A path may
 * follow the placeholder, such as {@code {lessons-repo}/tree/main/lessons/nested-tests}, to link to one folder. Where
 * an address is not set, the placeholder, its path and its dash are left out, so the point just describes the
 * repository and no address is ever assumed.
 */
final class RepoLinks {

    private static final Map<String, String> PLACEHOLDERS = Map.of(
            "{lessons-repo}", "video.lessonsRepo",
            "{toolkit-repo}", "video.toolkitRepo");

    private RepoLinks() {
    }

    static String fill(final String point) {
        String filled = point;
        for (final Map.Entry<String, String> placeholder : PLACEHOLDERS.entrySet()) {
            filled = filled(filled, placeholder.getKey(), System.getProperty(placeholder.getValue(), "").strip());
        }
        return filled;
    }

    /* The point with the placeholder replaced by its address, or, with no address set, left out with its dash. */
    private static String filled(final String point, final String placeholder, final String address) {
        final String withPath = Pattern.quote(placeholder) + "(\\S*)";
        if (!address.isEmpty()) {
            return point.replaceAll(withPath, Matcher.quoteReplacement(address) + "$1");
        }
        if (!point.contains(placeholder)) {
            return point;
        }
        final String rest = point.replaceAll(withPath, "").replaceFirst("^\\s*—\\s*", "").strip();
        return rest.isEmpty() ? rest : Character.toUpperCase(rest.charAt(0)) + rest.substring(1);
    }
}
