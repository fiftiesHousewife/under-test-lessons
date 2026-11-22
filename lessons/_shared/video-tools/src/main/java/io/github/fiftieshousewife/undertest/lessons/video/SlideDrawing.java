package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws a lesson's slides as images: each slide's HTML page, styled by the shared stylesheet and then any user
 * stylesheet (the system property {@code video.slideCss}), screenshotted by the browser into the voice folder.
 */
final class SlideDrawing {

    private SlideDrawing() {
    }

    /** Draws one slide as {@code <name>.png} in the lesson's voice folder, and returns the image. */
    static Path draw(final Media media, final String name, final Slide slide) {
        final Path folder = media.file(".voice");
        try {
            Files.createDirectories(folder);
            final Path page = folder.resolve(name + ".html");
            final List<Path> pictures = media.pictureFolders();
            Files.writeString(page, SlideHtml.page(slide, stylesheets(media), pictures));
            final Path png = folder.resolve(name + ".png");
            Browser.screenshot(page, png);
            return png;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Draws a slide once for each stage of its build-up, as {@code <name>-0.png}, {@code <name>-1.png} and so on: the
     * title alone, then one more point each time, the picture arriving with the first. A slide without points is one
     * stage, drawn whole.
     */
    static List<Path> drawStages(final Media media, final String name, final Slide slide) {
        final Path folder = media.file(".voice");
        final int points = slide.points().size();
        final List<Path> stages = new ArrayList<>();
        try {
            Files.createDirectories(folder);
            final String page = SlideHtml.page(slide, stylesheets(media), media.pictureFolders());
            for (int shown = 0; shown <= points; shown++) {
                final Path html = folder.resolve(name + "-" + shown + ".html");
                Files.writeString(html, points == 0 ? page : SlideHtml.stage(page, shown));
                final Path png = folder.resolve(name + "-" + shown + ".png");
                Browser.screenshot(html, png);
                stages.add(png);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return stages;
    }

    /** Draws every slide of a lesson, for a quick look without making the whole video. */
    static List<Path> drawAll(final Media media) {
        final Slides slides = media.slides();
        final List<Path> images = new ArrayList<>();
        for (int i = 0; i < slides.intro().size(); i++) {
            images.add(draw(media, "intro-" + (i + 1), slides.intro().get(i)));
        }
        for (int i = 0; i < slides.closing().size(); i++) {
            images.add(draw(media, "closing-" + (i + 1), slides.closing().get(i)));
        }
        return images;
    }

    static List<Path> stylesheets(final Media media) {
        final List<Path> sheets = new ArrayList<>(List.of(media.sharedSlides().resolve("slides.css")));
        final String user = System.getProperty("video.slideCss", "");
        if (!user.isEmpty()) {
            sheets.add(Path.of(user));
        }
        return sheets;
    }
}
