package io.github.fiftieshousewife.undertest.lessons.video;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
 * A deck's slide for one scene of the example: its notes as points, and its callouts, beside the code panels and
 * commands it shows; the narration, which the voice reads, is in the speaker notes. A scene with no notes shows its
 * narration.
 */
@SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION", justification = "Every piece of text is escaped")
final class SceneSlide {

    private SceneSlide() {
    }

    @SuppressFBWarnings(value = "POTENTIAL_XML_INJECTION", justification = "Every piece of text is escaped")
    static String section(final Slide scene, final SceneContent content, final Optional<String> cast) {
        final StringBuilder html = new StringBuilder("<section class=\"deck-slide\"><main class=\"slide scene\">"
                + "<section class=\"text\"><h1>").append(SlideHtml.escape(scene.title()))
                .append("</h1><div class=\"rule\"></div>");
        appendText(html, scene, content);
        html.append("</section><figure class=\"code\" style=\"--code-size: ").append(codeSize(content))
                .append("px\">");
        appendPanels(html, content);
        if (cast.isPresent()) {
            html.append("<div class=\"live-run cast\"><script type=\"application/x-asciicast\">")
                    .append(cast.get().replace("</", "<\\/")).append("</script></div>");
        } else {
            appendCommands(html, content);
        }
        return html.append("</figure></main><aside class=\"notes\">").append(SlideHtml.escape(scene.narration()))
                .append("</aside></section>\n").toString();
    }

    /* The scene's notes as points, or its narration where it has none, then its callouts. */
    private static void appendText(final StringBuilder html, final Slide scene, final SceneContent content) {
        final List<String> points = points(scene.title(), content.notes());
        if (points.isEmpty()) {
            html.append("<p class=\"scene-text\">").append(SlideHtml.escape(scene.narration())).append("</p>");
        } else {
            html.append("<ul class=\"scene-points\">");
            points.forEach(point -> html.append("<li class=\"point\">").append(SlideHtml.escape(point))
                    .append("</li>"));
            html.append("</ul>");
        }
        content.callouts().forEach(callout -> html.append("<p class=\"callout\">").append(SlideHtml.escape(callout))
                .append("</p>"));
    }

    private static void appendPanels(final StringBuilder html, final SceneContent content) {
        for (final SceneContent.Panel panel : content.panels()) {
            html.append("<figcaption>").append(SlideHtml.escape(panel.label())).append("</figcaption><pre>")
                    .append(String.join("\n", panel.lines().stream()
                            .map(line -> marked(line, panel.marked().contains(MarkedLines.lineNumber(line))))
                            .toList()))
                    .append("</pre>");
        }
    }

    private static void appendCommands(final StringBuilder html, final SceneContent content) {
        final List<List<String>> outputs = content.outputs();
        for (int i = 0; i < content.commands().size(); i++) {
            html.append("<pre class=\"command\">$ ").append(SlideHtml.escape(content.commands().get(i)))
                    .append("</pre>");
            if (i < outputs.size() && !outputs.get(i).isEmpty()) {
                final Set<Integer> keyLines = MarkedLines.marked(content);
                html.append("<pre class=\"output\">").append(String.join("\n", outputs.get(i).stream()
                        .map(line -> marked(line, MarkedLines.namesAny(line, keyLines))).toList())).append("</pre>");
            }
        }
    }

    /*
     * A scene's notes as points: each sentence or clause after a colon its own point, without the emphasis marks,
     * and without a first clause that only repeats the slide's title ("The code under test: ...").
     */
    static List<String> points(final String title, final List<String> notes) {
        final String heading = capitalised(title.strip());
        final List<String> points = new ArrayList<>();
        for (final String note : notes) {
            for (final String part : note.replace("*", "").split("(?<=[.:])\\s+")) {
                point(part, heading).ifPresent(points::add);
            }
        }
        return points;
    }

    private static Optional<String> point(final String part, final String heading) {
        final String point = capitalised(part.strip().replaceAll("[.:]$", "").strip());
        return point.isEmpty() || point.equals(heading) ? Optional.empty() : Optional.of(point);
    }

    private static String capitalised(final String text) {
        return text.isEmpty() ? text : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    /* One line of a panel or output, escaped, and highlighted when it is one the scene is about. */
    static String marked(final String line, final boolean key) {
        return key ? "<mark>" + SlideHtml.escape(line) + "</mark>" : SlideHtml.escape(line);
    }

    /* A font size, in pixels, that fits every panel's lines on the slide: at most 22, at least 12. */
    static int codeSize(final SceneContent content) {
        final List<String> lines = new ArrayList<>();
        content.panels().forEach(panel -> lines.addAll(panel.lines()));
        content.commands().forEach(command -> lines.add("$ " + command));
        content.outputs().forEach(lines::addAll);
        final int rows = lines.size() + 2 * content.panels().size();
        final int widest = lines.stream().mapToInt(String::length).max().orElse(1);
        final double byHeight = 860.0 / Math.max(1, rows) / 1.35;
        final double byWidth = 1080.0 / Math.max(1, widest) / 0.6;
        return (int) Math.max(12, Math.min(22, Math.min(byHeight, byWidth)));
    }
}
