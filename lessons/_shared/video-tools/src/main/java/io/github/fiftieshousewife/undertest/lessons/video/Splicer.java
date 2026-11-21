package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Turns slides into short video segments (the slide image with its narration) and joins segments into one video. All
 * inputs are brought to the same size, frame rate and sound format first, so any mix of slides and recordings joins.
 */
final class Splicer {

    /** Time a slide stays up after its narration ends. */
    static final double SLIDE_BREATH_SECONDS = 1.5;

    /** A video file and its length in seconds. */
    record Segment(Path video, double seconds) {
    }

    /** The intro and closing slide segments around the recording. */
    record Timings(List<Segment> intro, List<Segment> closing) {

        /** Keeps its own copies of the lists. */
        Timings {
            intro = List.copyOf(intro);
            closing = List.copyOf(closing);
        }

        double introSeconds() {
            return intro.stream().mapToDouble(Segment::seconds).sum();
        }
    }

    private Splicer() {
    }

    /** Draws a slide and joins it to its spoken clip ({@code <name>.wav} in the voice folder) as a segment. */
    static Segment slide(final Media media, final String name, final Slide slide) {
        final Path voice = media.file(".voice");
        final Path png = SlideDrawing.draw(media, name, slide);
        final double seconds = WavFile.read(voice.resolve(name + ".wav")).seconds() + SLIDE_BREATH_SECONDS;
        final Path video = voice.resolve(name + ".mp4");
        ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error", "-loop", "1",
                "-framerate", "30", "-i", png.toString(), "-i", voice.resolve(name + ".wav").toString(),
                "-af", "apad", "-t", String.format(Locale.ROOT, "%.3f", seconds), "-c:v", "libx264",
                "-pix_fmt", "yuv420p", "-c:a", "aac", "-ar", "48000", "-ac", "2", video.toString()), voice);
        return new Segment(video, seconds);
    }

    /** Joins videos, each with sound, end to end into one. */
    static void join(final List<Path> videos, final Path out) {
        final List<String> command = new ArrayList<>(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error"));
        final StringBuilder filter = new StringBuilder();
        for (int i = 0; i < videos.size(); i++) {
            command.add("-i");
            command.add(videos.get(i).toString());
            filter.append(String.format(Locale.ROOT, "[%d:v]scale=%d:%d,setsar=1,fps=30,format=yuv420p[v%d];"
                    + "[%d:a]aformat=sample_rates=48000:channel_layouts=stereo[a%d];",
                    i, SlideHtml.WIDTH, SlideHtml.HEIGHT, i, i, i));
        }
        for (int i = 0; i < videos.size(); i++) {
            filter.append(String.format(Locale.ROOT, "[v%d][a%d]", i, i));
        }
        filter.append(String.format(Locale.ROOT, "concat=n=%d:v=1:a=1[v][a]", videos.size()));
        command.addAll(List.of("-filter_complex", filter.toString(), "-map", "[v]", "-map", "[a]",
                "-c:v", "libx264", "-pix_fmt", "yuv420p", "-c:a", "aac", "-b:a", "128k", out.toString()));
        ExternalCommand.run(command, Path.of("").toAbsolutePath());
    }
}
