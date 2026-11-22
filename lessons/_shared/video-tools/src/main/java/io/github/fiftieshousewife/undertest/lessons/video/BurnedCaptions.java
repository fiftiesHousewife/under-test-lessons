package io.github.fiftieshousewife.undertest.lessons.video;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;

/**
 * Captions burned into the picture, for players that hide a video's captions track until it is switched on. Each
 * caption is drawn, in the look's colours, as a see-through picture the size of the frame with the text on a band near
 * the bottom; ffmpeg lays each over the video only while it is spoken. Drawn here rather than by ffmpeg's subtitles
 * filter, which many ffmpeg builds (and company machines) don't have. The captions track and files are kept as well.
 */
final class BurnedCaptions {

    private static final int WIDTH = SlideHtml.WIDTH;
    private static final int HEIGHT = SlideHtml.HEIGHT;
    private static final int FONT_SIZE = 44;
    private static final int LINE_HEIGHT = 58;
    private static final int PADDING_X = 36;
    private static final int PADDING_Y = 18;
    private static final int BOTTOM_MARGIN = 56;
    private static final int CORNER = 18;

    /** The colours of the caption text and of the band behind it. */
    record Look(Color text, Color band) {
    }

    private BurnedCaptions() {
    }

    /** Black ink on a newsprint band in the newspaper look; white on a dark band otherwise. */
    static Look look() {
        return System.getProperty("video.slideCss", "").contains("newspaper")
                ? new Look(new Color(0x11, 0x11, 0x11), new Color(0xF3, 0xEF, 0xE4, 235))
                : new Look(Color.WHITE, new Color(0, 0, 0, 185));
    }

    /** Where the band's top edge is for a caption of that many lines. */
    static int bandTop(final int lines) {
        return HEIGHT - BOTTOM_MARGIN - lines * LINE_HEIGHT - 2 * PADDING_Y;
    }

    /** A caption drawn as a see-through picture the size of the frame: its text, wrapped, on a band near the bottom. */
    static BufferedImage picture(final String text, final Look look) {
        final List<String> lines = CaptionFile.wrap(text).lines().toList();
        final BufferedImage picture = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = picture.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE));
            final FontMetrics metrics = graphics.getFontMetrics();
            final int widest = lines.stream().mapToInt(metrics::stringWidth).max().orElse(0);
            final int top = bandTop(lines.size());
            graphics.setColor(look.band());
            graphics.fillRoundRect((WIDTH - widest) / 2 - PADDING_X, top, widest + 2 * PADDING_X,
                    lines.size() * LINE_HEIGHT + 2 * PADDING_Y, CORNER, CORNER);
            graphics.setColor(look.text());
            for (int i = 0; i < lines.size(); i++) {
                final int baseline = top + PADDING_Y + i * LINE_HEIGHT + (LINE_HEIGHT + metrics.getAscent()
                        - metrics.getDescent()) / 2;
                graphics.drawString(lines.get(i), (WIDTH - metrics.stringWidth(lines.get(i))) / 2, baseline);
            }
        } finally {
            graphics.dispose();
        }
        return picture;
    }

    /** Writes {@code out}: the video with each cue's caption burned in while it is spoken, and its sound unchanged. */
    static void burn(final Path video, final List<Cue> cues, final Path out) {
        final Path folder = out.resolveSibling(out.getFileName() + ".captions");
        createFolder(folder);
        final Look look = look();
        final List<String> command = new ArrayList<>(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error",
                "-i", video.toString()));
        final StringBuilder filter = new StringBuilder();
        String last = "0:v";
        for (int i = 0; i < cues.size(); i++) {
            final Path picture = folder.resolve(String.format(Locale.ROOT, "%03d.png", i + 1));
            write(picture(cues.get(i).text(), look), picture);
            command.addAll(List.of("-i", picture.toString()));
            final String next = "c" + (i + 1);
            filter.append(String.format(Locale.ROOT, "[%s][%d:v]overlay=0:0:enable='between(t,%.3f,%.3f)'[%s];",
                    last, i + 1, cues.get(i).start(), cues.get(i).end(), next));
            last = next;
        }
        if (cues.isEmpty()) {
            command.addAll(List.of("-c", "copy", out.toString()));
        } else {
            command.addAll(List.of("-filter_complex", filter.substring(0, filter.length() - 1), "-map", "[" + last
                    + "]", "-map", "0:a?", "-c:v", "libx264", "-pix_fmt", "yuv420p", "-c:a", "copy", out.toString()));
        }
        ExternalCommand.run(command, Path.of("").toAbsolutePath());
    }

    private static void write(final BufferedImage picture, final Path file) {
        try {
            ImageIO.write(picture, "png", file.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void createFolder(final Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
