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
    /** How long a slide takes to fade up from black, and back down to black. */
    static final double FADE_SECONDS = 1.0 / 3;
    /** The share of a slide's narration over which its points appear, one after another. */
    static final double BUILD_UP = 0.8;
    /** How long each point takes to fade in. */
    static final double CROSSFADE_SECONDS = 0.3;
    /** How much a slide grows over the time it shows: a slow, gentle zoom towards its centre. */
    static final double SLIDE_ZOOM = 0.04;

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

    /** A slide's stages (one picture each), its spoken clip, and how long the clip speaks for, in seconds. */
    record Cut(List<Path> stages, Path voice, double spoken) {

        /** Keeps its own copy of the stages. */
        Cut {
            stages = List.copyOf(stages);
        }

        double seconds() {
            return spoken + SLIDE_BREATH_SECONDS;
        }
    }

    /** Draws a slide, stage by stage, and joins it to its spoken clip ({@code <name>.wav}) as a segment. */
    static Segment slide(final Media media, final String name, final Slide slide) {
        final Path voice = media.file(".voice");
        final Path wav = voice.resolve(name + ".wav");
        final Cut cut = new Cut(SlideDrawing.drawStages(media, name, slide), wav, WavFile.read(wav).seconds());
        final Path video = voice.resolve(name + ".mp4");
        ExternalCommand.run(command(cut, video), voice);
        return new Segment(video, cut.seconds());
    }

    /**
     * The ffmpeg command that makes a slide's segment: each stage in turn, crossfading into the next as its point
     * appears ({@link #appearances}), with the slide's {@link #motion} over the whole, and its voice. The segment lasts
     * exactly {@link Cut#seconds()}.
     */
    static List<String> command(final Cut cut, final Path video) {
        final double seconds = cut.seconds();
        final List<Double> appear = appearances(cut.stages().size() - 1, cut.spoken());
        final List<String> command = new ArrayList<>(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error"));
        final StringBuilder filter = new StringBuilder();
        for (int i = 0; i < cut.stages().size(); i++) {
            final double offset = i == 0 ? 0 : offset(appear.get(i - 1));
            final double length = i == cut.stages().size() - 1 ? seconds - offset : seconds;
            command.addAll(List.of("-loop", "1", "-framerate", "30", "-t", number(length), "-i",
                    cut.stages().get(i).toString()));
            filter.append(String.format(Locale.ROOT, "[%d:v]fps=30,format=yuv420p,settb=AVTB[s%d];", i, i));
        }
        String last = "s0";
        for (int i = 1; i < cut.stages().size(); i++) {
            filter.append(String.format(Locale.ROOT, "[%s][s%d]xfade=transition=fade:duration=%s:offset=%s[x%d];",
                    last, i, number(CROSSFADE_SECONDS), number(offset(appear.get(i - 1))), i));
            last = "x" + i;
        }
        filter.append('[').append(last).append(']').append(motion(seconds)).append("[v]");
        command.addAll(List.of("-i", cut.voice().toString(), "-filter_complex", filter.toString(), "-map", "[v]",
                "-map", cut.stages().size() + ":a", "-af", "apad", "-t", number(seconds), "-c:v", "libx264",
                "-pix_fmt", "yuv420p", "-c:a", "aac", "-ar", "48000", "-ac", "2", video.toString()));
        return command;
    }

    /** When each of a slide's points appears: evenly through the first {@link #BUILD_UP} of its narration. */
    static List<Double> appearances(final int points, final double spoken) {
        final List<Double> times = new ArrayList<>();
        for (int point = 1; point <= points; point++) {
            times.add(BUILD_UP * spoken * point / points);
        }
        return times;
    }

    /** Where a crossfade starts, so it is half done when its point appears. */
    private static double offset(final double appears) {
        return Math.max(0.01, appears - CROSSFADE_SECONDS / 2);
    }

    /**
     * The movement on a slide that shows for this long: a slow zoom of {@link #SLIDE_ZOOM} towards its centre, and a
     * fade up from black at the start and down to black at the end. Every frame stays the slide's size, and the
     * length doesn't change, so captions and voice stay in time. Too short a slide gets the zoom without the fades.
     */
    static String motion(final double seconds) {
        final String zoom = String.format(Locale.ROOT, "(1+%s*t/%s)", SLIDE_ZOOM, number(seconds));
        final String moving = String.format(Locale.ROOT, "scale=w='trunc((iw*%1$s+1)/2)*2':h='trunc((ih*%1$s+1)/2)*2'"
                + ":eval=frame,crop=w=%2$d:h=%3$d:x='(in_w-%2$d)/2':y='(in_h-%3$d)/2'", zoom, SlideHtml.WIDTH,
                SlideHtml.HEIGHT);
        if (seconds < 3 * FADE_SECONDS) {
            return moving;
        }
        return moving + String.format(Locale.ROOT, ",fade=t=in:st=0:d=%1$s,fade=t=out:st=%2$s:d=%1$s",
                number(FADE_SECONDS), number(seconds - FADE_SECONDS));
    }

    private static String number(final double value) {
        return String.format(Locale.ROOT, "%.3f", value);
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
