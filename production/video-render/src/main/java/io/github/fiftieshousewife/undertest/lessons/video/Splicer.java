package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Turns slides into short video segments (the slide image with its narration) and joins segments into one video. All
 * inputs are brought to the same size, frame rate and sound format first, so any mix of slides and recordings joins. A
 * slide fades up from black, and back down, over {@code FADE_SECONDS}; its points appear one after another over the
 * first {@code BUILD_UP} share of its narration, each fading in over {@code CROSSFADE_SECONDS}; and it grows by
 * {@code SLIDE_ZOOM} as it shows, a slow, gentle zoom towards its centre.
 */
final class Splicer {

    static final double FADE_SECONDS = 1.0 / 3;
    static final double BUILD_UP = 0.8;
    static final double CROSSFADE_SECONDS = 0.3;
    static final double SLIDE_ZOOM = 0.04;

    /* A video file and its length in seconds. */
    record Segment(Path video, double seconds) {
    }

    /* The intro and closing slide segments around the recording. */
    record Timings(List<Segment> intro, List<Segment> closing) {

        /* Keeps its own copies of the lists. */
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

    /* A slide's stages (one picture each), its spoken clip, and how long the clip speaks for, in seconds. */
    record Cut(List<Path> stages, Path voice, double spoken) {

        /* Keeps its own copy of the stages. */
        Cut {
            stages = List.copyOf(stages);
        }

        double seconds() {
            return spoken + Slide.BREATH_SECONDS;
        }
    }

    /* Draws a slide, stage by stage, and joins it to its spoken clip ({@code <name>.wav}) as a segment. */
    static Segment slide(final Media media, final String name, final Slide slide) {
        final Path voice = media.file(".voice");
        final Path wav = voice.resolve(name + ".wav");
        final Cut cut = new Cut(SlideDrawing.drawStages(media, name, slide), wav, WavFile.read(wav).seconds());
        final Path video = voice.resolve(name + ".mp4");
        ExternalCommand.run(command(cut, video), voice);
        return new Segment(video, cut.seconds());
    }

    /*
     * The ffmpeg command that makes a slide's segment: each stage in turn, crossfading into the next as its point
     * appears ({@link #appearances}), with the slide's {@link #motion} over the whole, and its voice. The segment lasts
     * exactly {@link Cut#seconds()}.
     */
    static List<String> command(final Cut cut, final Path video) {
        final double seconds = cut.seconds();
        final List<Double> appear = appearances(cut.stages().size() - 1, cut.spoken());
        final List<String> command = new ArrayList<>(List.of(Ffmpeg.program(), "-y", "-loglevel", "error"));
        final StringBuilder filter = new StringBuilder();
        for (int i = 0; i < cut.stages().size(); i++) {
            command.addAll(stageInput(cut, appear, i));
            filter.append(String.format(Locale.ROOT, "[%d:v]fps=30,format=yuv420p,settb=AVTB[s%d];", i, i));
        }
        filter.append(crossfades(cut, appear)).append(motion(seconds)).append("[v]");
        command.addAll(List.of("-i", cut.voice().toString(), "-filter_complex", filter.toString(), "-map", "[v]",
                "-map", cut.stages().size() + ":a", "-af", "apad", "-t", number(seconds), "-c:v", "libx264",
                "-pix_fmt", "yuv420p", "-c:a", "aac", "-ar", "48000", "-ac", "2", video.toString()));
        return command;
    }

    /* The input for one stage: its picture, shown from when its point appears to the end. */
    private static List<String> stageInput(final Cut cut, final List<Double> appear, final int stage) {
        final double offset = stage == 0 ? 0 : offset(appear.get(stage - 1));
        final double length = stage == cut.stages().size() - 1 ? cut.seconds() - offset : cut.seconds();
        return List.of("-loop", "1", "-framerate", "30", "-t", number(length), "-i",
                cut.stages().get(stage).toString());
    }

    /* Each stage crossfading into the next as its point appears, then the name of the last: {@code ...[x2]}. */
    private static String crossfades(final Cut cut, final List<Double> appear) {
        final StringBuilder filter = new StringBuilder();
        String last = "s0";
        for (int i = 1; i < cut.stages().size(); i++) {
            filter.append(String.format(Locale.ROOT, "[%s][s%d]xfade=transition=fade:duration=%s:offset=%s[x%d];",
                    last, i, number(CROSSFADE_SECONDS), number(offset(appear.get(i - 1))), i));
            last = "x" + i;
        }
        return filter.append('[').append(last).append(']').toString();
    }

    /* When each of a slide's points appears: evenly through the first {@link #BUILD_UP} of its narration. */
    static List<Double> appearances(final int points, final double spoken) {
        final List<Double> times = new ArrayList<>();
        for (int point = 1; point <= points; point++) {
            times.add(BUILD_UP * spoken * point / points);
        }
        return times;
    }

    /* Where a crossfade starts, so it is half done when its point appears. */
    private static double offset(final double appears) {
        return Math.max(0.01, appears - CROSSFADE_SECONDS / 2);
    }

    /*
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
}
