package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for making a lesson video:
 * <ul>
 *   <li>{@code record <root> <id>}: records the silent video ({@code <id>.silent.mp4} and the GIF) with VHS;</li>
 *   <li>{@code voice <root> <id> <command...>}: reads each scene's narration aloud with the given command (which turns
 *   a text file into a WAV file), checks each clip fits its scene, and writes {@code <id>.narration.wav};</li>
 *   <li>{@code assemble <root> <id>}: combines the silent video, the narration and the captions into {@code <id>.mp4},
 *   and writes the narration as {@code <id>.narration.m4a} too.</li>
 * </ul>
 * Scene times come from the script and are stretched to the recorded video's real length.
 */
@Slf4j
public final class LessonVideo {

    private LessonVideo() {
    }

    public static void main(final String... args) {
        if (args.length < 3 || !List.of("record", "voice", "assemble", "slides", "deck").contains(args[0])) {
            throw new IllegalArgumentException("usage: LessonVideo record <root> <lesson>"
                    + " | voice <root> <lesson> <command...> | assemble <root> <lesson> | slides <root> <lesson>"
                    + " | deck <root> <lesson|all> [pdf]");
        }
        if ("deck".equals(args[0])) {
            final boolean pdf = args.length > 3 && "pdf".equals(args[3]);
            SlideDeck.lessons(Path.of(args[1]), args[2]).forEach(media -> SlideDeck.write(media, pdf));
            return;
        }
        final Media media = new Media(Path.of(args[1]), args[2]);
        switch (args[0]) {
            case "record" -> new Recorder(Clock.systemUTC()).record(media);
            case "voice" -> voice(media, List.of(Arrays.copyOfRange(args, 3, args.length)));
            case "slides" -> SlideDeck.lessons(media.root(), args[2]).forEach(lesson -> log.info(
                    "{}: {} slides drawn, in the lesson's voice folder", lesson.safeLesson(),
                    SlideDrawing.drawAll(lesson).size()));
            default -> assemble(media);
        }
    }

    static void voice(final Media media, final List<String> command) {
        if (command.isEmpty()) {
            throw new IllegalArgumentException("give the voice command: a program that turns a text file into a WAV");
        }
        final List<Scene> scenes = media.scenes();
        final Pronunciation pronunciation =
                Pronunciation.read(media.root().resolve("lessons/_shared/pronunciations.txt"));
        final List<String> narration = Narration.paragraphs(media.file(".narration.md")).stream()
                .map(pronunciation::forSpeech).toList();
        final Path clipsFolder = media.file(".voice");
        createFolder(clipsFolder);
        final List<WavFile> clips = new ArrayList<>();
        for (int i = 0; i < narration.size(); i++) {
            clips.add(speak(command, narration.get(i), clipsFolder.resolve(String.format("%02d", i + 1))));
        }
        final List<String> misfits = Voiceover.misfits(scenes, clips);
        if (!misfits.isEmpty()) {
            throw new IllegalStateException("The narration does not fit the video:\n  " + String.join("\n  ", misfits));
        }
        Voiceover.track(scenes, clips).write(media.file(".narration.wav"));
        final Slides slides = media.slides();
        final Voice voice = new Voice(command, pronunciation);
        speakSlides(voice, slides.intro(), clipsFolder.resolve("intro"));
        speakSlides(voice, slides.closing(), clipsFolder.resolve("closing"));
        log.info("{}: narration for {} scenes and {} slides written", media.safeLesson(), clips.size(),
                slides.intro().size() + slides.closing().size());
    }

    static void assemble(final Media media) {
        final Path folder = media.folder();
        ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error",
                "-i", media.name(".silent.mp4"), "-i", media.name(".narration.wav"),
                "-map", "0:v", "-map", "1:a", "-c:v", "copy", "-c:a", "aac", "-b:a", "128k", media.name(".main.mp4")),
                folder);
        final Slides slides = media.slides();
        final List<Splicer.Segment> intro = segments(media, slides.intro(), "intro");
        final List<Splicer.Segment> closing = segments(media, slides.closing(), "closing");
        final List<Path> videos = new ArrayList<>(intro.stream().map(Splicer.Segment::video).toList());
        videos.add(media.file(".main.mp4"));
        videos.addAll(closing.stream().map(Splicer.Segment::video).toList());
        Splicer.join(videos, media.file(".joined.mp4"));
        final List<Cue> cues = SpliceCaptions.cues(media, slides, new Splicer.Timings(intro, closing));
        writeText(media.file(".vtt"), CaptionFile.webVtt(cues));
        writeText(media.file(".srt"), CaptionFile.srt(cues));
        ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error",
                "-i", media.name(".joined.mp4"), "-i", media.name(".vtt"), "-map", "0", "-map", "1", "-c", "copy",
                "-c:s", "mov_text", "-metadata:s:s:0", "language=eng", media.name(".mp4")), folder);
        ExternalCommand.run(List.of(ExternalCommand.ffmpeg(), "-y", "-loglevel", "error",
                "-i", media.name(".joined.mp4"), "-vn", "-c:a", "aac", "-b:a", "128k", media.name(".narration.m4a")),
                folder);
        log.info("{}: finished video written: {} intro slides, the recording, {} closing slides, voice and captions",
                media.safeLesson(), intro.size(), closing.size());
    }

    private static List<Splicer.Segment> segments(final Media media, final List<Slide> slides, final String kind) {
        final List<Splicer.Segment> segments = new ArrayList<>();
        for (int i = 0; i < slides.size(); i++) {
            segments.add(Splicer.slide(media, kind + "-" + (i + 1), slides.get(i)));
        }
        return segments;
    }

    /** The voice command and how it should say awkward words. */
    private record Voice(List<String> command, Pronunciation pronunciation) {
    }

    private static void speakSlides(final Voice voice, final List<Slide> slides, final Path prefix) {
        for (int i = 0; i < slides.size(); i++) {
            speak(voice.command(), voice.pronunciation().forSpeech(slides.get(i).narration()),
                    prefix.resolveSibling(prefix.getFileName() + "-" + (i + 1)));
        }
    }

    private static void writeText(final Path file, final String text) {
        try {
            Files.writeString(file, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Speaks a scene's narration: each part between pause markers separately, joined with the pauses' silence. */
    private static WavFile speak(final List<String> command, final String narration, final Path clip) {
        final List<Pauses.Part> parts = Pauses.parts(narration);
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("a scene has no narration: " + clip);
        }
        final java.io.ByteArrayOutputStream joined = new java.io.ByteArrayOutputStream();
        final List<WavFile> spoken = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            final Path partClip = clip.resolveSibling(clip.getFileName() + "-" + (i + 1));
            spoken.add(speakPart(command, parts.get(i).text(), partClip));
            joined.writeBytes(spoken.getLast().samples());
            joined.writeBytes(spoken.getLast().silence(parts.get(i).pauseAfter()).samples());
        }
        final WavFile format = spoken.getFirst();
        final WavFile whole = new WavFile(format.sampleRate(), format.channels(), joined.toByteArray());
        whole.write(clip.resolveSibling(clip.getFileName() + ".wav"));
        return whole;
    }

    private static WavFile speakPart(final List<String> command, final String text, final Path clip) {
        final Path textFile = clip.resolveSibling(clip.getFileName() + ".txt");
        final Path wav = clip.resolveSibling(clip.getFileName() + ".wav");
        try {
            Files.writeString(textFile, text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        final List<String> full = new ArrayList<>(command);
        full.add(textFile.toAbsolutePath().toString());
        full.add(wav.toAbsolutePath().toString());
        ExternalCommand.run(full, Path.of("").toAbsolutePath());
        return WavFile.read(wav);
    }

    private static void createFolder(final Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
