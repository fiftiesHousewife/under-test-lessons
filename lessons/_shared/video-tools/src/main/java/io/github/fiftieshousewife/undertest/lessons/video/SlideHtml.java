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
    private static final java.util.regex.Pattern POINT = java.util.regex.Pattern.compile("<li class=\"(point|link)\">");

    private SlideHtml() {
    }

    /** The page for a slide, styled by the given stylesheets in order, each picture from the first folder with it. */
    static String page(final Slide slide, final List<Path> stylesheets, final List<Path> pictures) {
        return "<!doctype html><html><head><meta charset=\"utf-8\"><style>\n" + styles(stylesheets)
                + "</style></head><body class=\"" + frameClass() + "\">" + definitions(pictures)
                + body(slide, pictures, "") + "</body></html>\n";
    }

    /** Shared picture definitions (hatching, a hand-drawn wobble) that stylesheets can use, if the folder has them. */
    static String definitions(final List<Path> pictures) {
        return pictures.stream().map(folder -> folder.resolve("defs.svg")).filter(Files::isRegularFile).findFirst()
                .map(SlideHtml::read).orElse("");
    }

    /** A picture, from the first folder that has it. */
    static Path picture(final List<Path> pictures, final String name) {
        return pictures.stream().map(folder -> folder.resolve(name + ".svg")).filter(Files::isRegularFile)
                .findFirst().orElseGet(() -> pictures.getLast().resolve(name + ".svg"));
    }

    /** How pictures are framed (video.pictureFrame: soft, none or circle), as a class stylesheets can style. */
    static String frameClass() {
        final String frame = System.getProperty("video.pictureFrame", "soft").strip();
        return "frame-" + (frame.matches("soft|none|circle") ? frame : "soft");
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
    static String body(final Slide slide, final List<Path> pictures, final String paragraph) {
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
                    .append(read(picture(pictures, slide.heading().picture()))).append("</figure>");
        }
        return html.append("</main>").toString();
    }

    /**
     * One stage of a slide that builds up: its first {@code shown} points as they are, the rest hidden but keeping
     * their places ({@code later}), and its picture hidden until the first point arrives ({@code before-picture}).
     */
    static String stage(final String page, final int shown) {
        final java.util.regex.Matcher item = POINT.matcher(page);
        final StringBuilder out = new StringBuilder();
        int index = 0;
        while (item.find()) {
            item.appendReplacement(out, index++ < shown ? "$0" : "<li class=\"$1 later\">");
        }
        item.appendTail(out);
        return shown > 0 ? out.toString() : out.toString().replaceFirst("<body class=\"([^\"]*)\">",
                "<body class=\"$1 before-picture\">");
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
