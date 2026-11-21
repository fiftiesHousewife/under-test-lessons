package io.github.fiftieshousewife.undertest.lessons.video;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Builds a slide as a small HTML page: the title and points on the left, the slide's picture (an SVG file, placed
 * inline so stylesheets can colour it) on the right. The look comes from stylesheets: the shared default, then any the
 * user supplies, so an organisation can restyle every slide without touching the slides themselves.
 */
final class SlideHtml {

    static final int WIDTH = 1920;
    static final int HEIGHT = 1080;

    private SlideHtml() {
    }

    /** The page for a slide, styled by the given stylesheets in order, with pictures from the given folder. */
    @SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION",
            justification = "Stylesheets and pictures are this repo's own files, placed as is; slide text is escaped")
    static String page(final Slide slide, final List<Path> stylesheets, final Path pictures) {
        final StringBuilder html = new StringBuilder("<!doctype html><html><head><meta charset=\"utf-8\"><style>\n");
        stylesheets.forEach(css -> html.append(read(css)).append('\n'));
        html.append("</style></head><body><main class=\"slide\"><section class=\"text\"><h1>")
                .append(escape(slide.title())).append("</h1><div class=\"rule\"></div><ul>");
        for (final String point : slide.points()) {
            final boolean link = point.startsWith("https://") || point.startsWith("http://");
            html.append("<li class=\"").append(link ? "link" : "point").append("\">").append(escape(point))
                    .append("</li>");
        }
        html.append("</ul></section>");
        if (!slide.heading().picture().isEmpty()) {
            html.append("<figure class=\"picture\">")
                    .append(read(pictures.resolve(slide.heading().picture() + ".svg"))).append("</figure>");
        }
        return html.append("</main></body></html>\n").toString();
    }

    static String escape(final String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String read(final Path file) {
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("not found: " + file);
        }
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
