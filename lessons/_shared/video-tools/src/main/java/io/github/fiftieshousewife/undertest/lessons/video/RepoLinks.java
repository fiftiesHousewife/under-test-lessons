package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.Map;

/**
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
            final String address = System.getProperty(placeholder.getValue(), "").strip();
            if (!address.isEmpty()) {
                filled = filled.replace(placeholder.getKey(), address);
            } else if (filled.contains(placeholder.getKey())) {
                final String rest = filled.replace(placeholder.getKey(), "").replaceFirst("^\\s*—\\s*", "").strip();
                filled = rest.isEmpty() ? rest : Character.toUpperCase(rest.charAt(0)) + rest.substring(1);
            }
        }
        return filled;
    }
}
