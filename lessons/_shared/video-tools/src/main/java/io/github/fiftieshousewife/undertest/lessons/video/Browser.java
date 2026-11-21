package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Takes a full HD screenshot of an HTML page with a headless browser: the system property {@code video.browser}
 * (the build sets it to Google Chrome where installed, or -PlessonBrowser), or {@code google-chrome} on the path.
 */
final class Browser {

    private Browser() {
    }

    static String program() {
        final String chosen = System.getProperty("video.browser", "");
        return chosen.isEmpty() ? "google-chrome" : chosen;
    }

    /** Screenshots the page at the size of a slide, using a throwaway browser profile. */
    static void screenshot(final Path page, final Path png) {
        try {
            final Path profile = Files.createTempDirectory("slide-browser");
            ExternalCommand.run(List.of(program(), "--headless=new", "--disable-gpu", "--hide-scrollbars",
                    "--no-first-run", "--user-data-dir=" + profile, "--window-size=" + SlideHtml.WIDTH + ","
                    + SlideHtml.HEIGHT, "--screenshot=" + png.toAbsolutePath(), page.toUri().toString()),
                    Path.of("").toAbsolutePath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
