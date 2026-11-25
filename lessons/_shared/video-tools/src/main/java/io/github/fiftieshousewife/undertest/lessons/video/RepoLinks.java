package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.Map;

/*
 * Fills in the addresses of the lessons and toolkit repositories on slides, from the system properties
 * {@code video.lessonsRepo} and {@code video.toolkitRepo} (the build's lessonsRepoUrl and toolkitRepoUrl). Where one is
 * not set, the placeholder and its dash are left out, so the point just describes the repository and no address is
 * ever assumed.
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
        if (!address.isEmpty()) {
            return point.replace(placeholder, address);
        }
        if (!point.contains(placeholder)) {
            return point;
        }
        final String rest = point.replace(placeholder, "").replaceFirst("^\\s*—\\s*", "").strip();
        return rest.isEmpty() ? rest : Character.toUpperCase(rest.charAt(0)) + rest.substring(1);
    }
}
