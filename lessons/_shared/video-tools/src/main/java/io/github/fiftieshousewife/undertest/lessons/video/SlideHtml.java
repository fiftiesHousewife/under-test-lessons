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
    static String page(final Slide slide, final List<Path> stylesheets, final Path pictures) {
        return "<!doctype html><html><head><meta charset=\"utf-8\"><style>\n" + styles(stylesheets)
                + "</style></head><body>" + definitions(pictures) + body(slide, pictures, "") + "</body></html>\n";
    }

    /** Shared picture definitions (hatching, a hand-drawn wobble) that stylesheets can use, if the folder has them. */
    static String definitions(final Path pictures) {
        final Path defs = pictures.resolve("defs.svg");
        return Files.isRegularFile(defs) ? read(defs) : "";
    }

    /** The stylesheets, one after another, so later ones override earlier ones. */
    static String styles(final List<Path> stylesheets) {
        final StringBuilder css = new StringBuilder();
        stylesheets.forEach(sheet -> css.append(read(sheet)).append('\n'));
        return css.toString();
    }

    /** One slide: title, points (or the given paragraph, already escaped), and the picture beside them. */
    @SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION",
            justification = "Pictures are this repo's own files, placed as is; slide text is escaped")
    static String body(final Slide slide, final Path pictures, final String paragraph) {
        final StringBuilder html = new StringBuilder("<main class=\"slide\"><section class=\"text\"><h1>")
                .append(escape(slide.title())).append("</h1><div class=\"rule\"></div><ul>");
        for (final String point : slide.points()) {
            final boolean link = point.startsWith("https://") || point.startsWith("http://");
            html.append("<li class=\"").append(link ? "link" : "point").append("\">").append(escape(point))
                    .append("</li>");
        }
        html.append("</ul>").append(paragraph).append("</section>");
        if (!slide.heading().picture().isEmpty()) {
            html.append("<figure class=\"picture\">")
                    .append(read(pictures.resolve(slide.heading().picture() + ".svg"))).append("</figure>");
        }
        return html.append("</main>").toString();
    }

    static String escape(final String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    static String read(final Path file) {
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
