package io.github.fiftieshousewife.undertest.lessons.video;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Draws a slide as a full HD image in the videos' colours: a dark background like the terminal, the title in cyan
 * (the colour of narration in the terminal scenes), and the points in white, wrapped to fit.
 */
final class SlideImage {

    static final int WIDTH = 1920;
    static final int HEIGHT = 1080;
    private static final int MARGIN = 160;
    private static final Color BACKGROUND = new Color(0x17, 0x17, 0x17);
    private static final Color TITLE = new Color(0x5c, 0xe1, 0xe6);
    private static final Color TEXT = new Color(0xee, 0xee, 0xee);
    private static final Color ACCENT = new Color(0xe0, 0x6c, 0xd8);

    private SlideImage() {
    }

    static void draw(final Slide slide, final Path png) {
        final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(ACCENT);
        g.fillRect(MARGIN, 250, 120, 8);
        g.setColor(TITLE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 76));
        g.drawString(slide.title(), MARGIN, 220);
        g.setColor(TEXT);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 46));
        int y = 380;
        for (final String point : slide.points()) {
            final boolean link = point.startsWith("https://") || point.startsWith("http://");
            g.setColor(link ? ACCENT : TEXT);
            final List<String> lines = wrap(point, g.getFontMetrics(), WIDTH - 2 * MARGIN - 60);
            g.drawString(link ? "→" : "•", MARGIN, y);
            for (final String line : lines) {
                g.drawString(line, MARGIN + 60, y);
                y += 64;
            }
            y += 28;
        }
        g.dispose();
        try {
            ImageIO.write(image, "png", png.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static List<String> wrap(final String text, final FontMetrics metrics, final int width) {
        final List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (final String word : text.split(" ")) {
            final String tried = line.isEmpty() ? word : line + " " + word;
            if (!line.isEmpty() && metrics.stringWidth(tried) > width) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(tried);
            }
        }
        if (!line.isEmpty()) {
            lines.add(line.toString());
        }
        return lines;
    }
}
