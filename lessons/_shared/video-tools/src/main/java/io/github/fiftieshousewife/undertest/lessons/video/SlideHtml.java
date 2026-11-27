package io.github.fiftieshousewife.undertest.lessons.video;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/*
 * Builds a slide as a small HTML page: the title and points on the left, the slide's picture (an SVG file, placed
 * inline so stylesheets can colour it) on the right. The look comes from stylesheets: the shared default, then any the
 * user supplies, so an organisation can restyle every slide without touching the slides themselves. A point that links
 * a lesson's deck is written {@code lesson: Its name (its-id)}, with no address, so none is assumed.
 */
final class SlideHtml {

    static final int WIDTH = 1920;
    static final int HEIGHT = 1080;
    private static final java.util.regex.Pattern LESSON_LINK =
            java.util.regex.Pattern.compile("^lesson:\\s*(.+?)\\s*\\(([a-z0-9-]+)\\)$");

    private SlideHtml() {
    }

    /* The page for a slide, styled by the given stylesheets in order, each picture from the first folder with it. */
    static String page(final Slide slide, final List<Path> stylesheets, final List<Path> pictures) {
        return "<!doctype html><html><head><meta charset=\"utf-8\"><style>\n" + styles(stylesheets)
                + "</style></head><body class=\"" + frameClass() + "\">" + definitions(pictures)
                + body(slide, pictures, "") + "</body></html>\n";
    }

    /* Shared picture definitions (hatching, a hand-drawn wobble) that stylesheets can use, if the folder has them. */
    static String definitions(final List<Path> pictures) {
        return pictures.stream().map(folder -> folder.resolve("defs.svg")).filter(Files::isRegularFile).findFirst()
                .map(SlideHtml::read).orElse("");
    }

    /* A picture, from the first folder that has it. */
    static Path picture(final List<Path> pictures, final String name) {
        return pictures.stream().map(folder -> folder.resolve(name + ".svg")).filter(Files::isRegularFile)
                .findFirst().orElseGet(() -> pictures.getLast().resolve(name + ".svg"));
    }

    /* How pictures are framed (video.pictureFrame: soft, none or circle), as a class stylesheets can style. */
    static String frameClass() {
        final String frame = System.getProperty("video.pictureFrame", "soft").strip();
        return "frame-" + (frame.matches("soft|none|circle") ? frame : "soft");
    }

    /* The stylesheets, one after another, so later ones override earlier ones. */
    static String styles(final List<Path> stylesheets) {
        final StringBuilder css = new StringBuilder();
        stylesheets.forEach(sheet -> css.append(read(sheet)).append('\n'));
        return css.toString();
    }

    /* One slide: title, points (or the given paragraph, already escaped), and the picture beside them. */
    @SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION",
            justification = "Pictures are this repo's own files, placed as is; slide text is escaped")
    static String body(final Slide slide, final List<Path> pictures, final String paragraph) {
        final StringBuilder html = new StringBuilder("<main class=\"slide\"><section class=\"text\"><h1>")
                .append(escape(slide.title())).append("</h1><div class=\"rule\"></div><ul>");
        slide.points().forEach(point -> html.append(item(point)));
        html.append("</ul>").append(paragraph).append("</section>");
        if (!slide.heading().picture().isEmpty()) {
            html.append("<figure class=\"picture\">")
                    .append(read(picture(pictures, slide.heading().picture()))).append("</figure>");
        }
        return html.append("</main>").toString();
    }

    /* One point as a list item: a link to another lesson's deck, an address as a link, or plain text. */
    private static String item(final String point) {
        final java.util.regex.Matcher lesson = LESSON_LINK.matcher(point);
        if (lesson.matches()) {
            return "<li class=\"link lesson\"><a href=\"../../" + lesson.group(2) + "/media/" + lesson.group(2)
                    + ".deck.html#1\">" + escape(lesson.group(1)) + "</a></li>";
        }
        final boolean link = point.startsWith("https://") || point.startsWith("http://");
        return "<li class=\"" + (link ? "link" : "point") + "\">" + (link ? linked(point) : escape(point)) + "</li>";
    }

    /*
     * A point that starts with an address, escaped, with the address as a link that opens in a new tab: the deck can be
     * clicked, and a printed deck still shows the address.
     */
    static String linked(final String point) {
        final int end = point.indexOf(' ');
        final String address = end < 0 ? point : point.substring(0, end);
        return "<a href=\"" + escape(address) + "\" target=\"_blank\" rel=\"noopener\">" + escape(address) + "</a>"
                + escape(end < 0 ? "" : point.substring(end));
    }

    /* The ids of the lessons a slide links to. */
    static List<String> lessonLinks(final Slide slide) {
        return slide.points().stream().map(LESSON_LINK::matcher).filter(java.util.regex.Matcher::matches)
                .map(link -> link.group(2)).toList();
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

    /* The words, with each pause marker written as a direction: "(pause 1.6 s)". */
    static String withPauseDirections(final String written) {
        final StringBuilder out = new StringBuilder();
        for (final Pauses.Part part : Pauses.parts(written)) {
            out.append(escape(part.text()));
            if (part.pauseAfter() > 0) {
                out.append(" <span class=\"pause\">(pause ")
                        .append(String.format(Locale.ROOT, "%.1f", part.pauseAfter()).replace(".0", ""))
                        .append(" s)</span> ");
            }
        }
        return out.toString().strip();
    }
}
