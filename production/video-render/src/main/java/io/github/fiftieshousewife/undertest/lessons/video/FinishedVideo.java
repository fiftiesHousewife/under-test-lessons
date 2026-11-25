package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;

/*
 * A lesson's finished video, {@code <id>.mp4}: the intro slides, the recording with its narration and the closing
 * slides joined, with captions burned into the picture (unless {@code video.burnCaptions} is false) and as a captions
 * track; and the narration alone as {@code <id>.narration.m4a}.
 */
@Slf4j
final class FinishedVideo {

    private FinishedVideo() {
    }

    static void assemble(final Media media) {
        mainVideo(media);
        final Slides slides = media.slides();
        final List<Splicer.Segment> intro = segments(media, slides.intro(), "intro");
        final List<Splicer.Segment> closing = segments(media, slides.closing(), "closing");
        final List<Path> videos = new ArrayList<>(intro.stream().map(Splicer.Segment::video).toList());
        videos.add(media.file(".main.mp4"));
        videos.addAll(closing.stream().map(Splicer.Segment::video).toList());
        join(videos, media.file(".joined.mp4"));
        final List<Cue> cues = SpliceCaptions.cues(media, slides, new Splicer.Timings(intro, closing));
        writeText(media.file(".vtt"), CaptionFile.webVtt(cues));
        writeText(media.file(".srt"), CaptionFile.srt(cues));
        finish(media, cues);
        log.info("{}: finished video written: {} intro slides, the recording, {} closing slides, voice and captions",
                LogSafe.lesson(media), intro.size(), closing.size());
    }

    /* The recording, zoomed where the script zooms, with the narration: {@code <id>.main.mp4}. */
    private static void mainVideo(final Media media) {
        final String recording = String.valueOf(ZoomedRecording.zoomed(media).getFileName());
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error",
                "-i", recording, "-i", media.name(".narration.wav"),
                "-map", "0:v", "-map", "1:a", "-c:v", "copy", "-c:a", "aac", "-b:a", "128k", media.name(".main.mp4")),
                media.folder());
    }

    /* The joined video with its captions, burned in and as a track, as {@code <id>.mp4}; its sound as M4A. */
    private static void finish(final Media media, final List<Cue> cues) {
        final String picture = burnCaptions() ? burned(media, cues) : media.name(".joined.mp4");
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error",
                "-i", picture, "-i", media.name(".vtt"), "-map", "0", "-map", "1", "-c", "copy",
                "-c:s", "mov_text", "-metadata:s:s:0", "language=eng", media.name(".mp4")), media.folder());
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error",
                "-i", media.name(".joined.mp4"), "-vn", "-c:a", "aac", "-b:a", "128k", media.name(".narration.m4a")),
                media.folder());
    }

    /* Captions are burned into the picture unless {@code lessonBurnCaptions} is false. */
    private static boolean burnCaptions() {
        return Boolean.parseBoolean(System.getProperty("video.burnCaptions", "true"));
    }

    /* The joined video with its captions burned in, as {@code <id>.captioned.mp4}; returns its file name. */
    private static String burned(final Media media, final List<Cue> cues) {
        BurnedCaptions.burn(media.file(".joined.mp4"), cues, media.file(".captioned.mp4"));
        return media.name(".captioned.mp4");
    }

    private static List<Splicer.Segment> segments(final Media media, final List<Slide> slides, final String kind) {
        final List<Splicer.Segment> segments = new ArrayList<>();
        for (int i = 0; i < slides.size(); i++) {
            segments.add(Splicer.slide(media, kind + "-" + (i + 1), slides.get(i)));
        }
        return segments;
    }

    private static void writeText(final Path file, final String text) {
        try {
            Files.writeString(file, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /* Joins videos, each with sound, end to end into one. */
    static void join(final List<Path> videos, final Path out) {
        final List<String> command = new ArrayList<>(List.of(Ffmpeg.program(), "-y", "-loglevel", "error"));
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
