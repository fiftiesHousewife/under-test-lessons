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
 * Entry point for making a lesson video, on the machine that makes the videos (slides and decks: {@code LessonDecks}):
 * <ul>
 *   <li>{@code record <root> <id>}: records the silent video ({@code <id>.silent.mp4} and the GIF) with VHS;</li>
 *   <li>{@code voice <root> <id> <command...>}: reads each scene's narration aloud with the given command (which turns
 *   a text file into a WAV file), checks each clip fits its scene, and writes {@code <id>.narration.wav};</li>
 *   <li>{@code assemble <root> <id>}: combines the silent video, the narration and the captions (burned into the
 *   picture unless {@code video.burnCaptions} is false, and as a captions track) into {@code <id>.mp4},
 *   and writes the narration as {@code <id>.narration.m4a} too;</li>
 *   <li>{@code deckvoice <root> <id|all> <command...>}: reads each deck slide's narration aloud into
 *   {@code <id>.deck-voice/}, then rewrites the deck so it can play them ({@link DeckVoice}).</li>
 * </ul>
 * Scene times come from the script and are stretched to the recorded video's real length.
 */
@Slf4j
public final class LessonVideo {

    private LessonVideo() {
    }

    public static void main(final String... args) {
        final List<String> commands = List.of("record", "voice", "assemble", "deckvoice");
        if (args.length < 3 || !commands.contains(args[0])) {
            throw new IllegalArgumentException("usage: LessonVideo record <root> <lesson>"
                    + " | voice <root> <lesson> <command...> | assemble <root> <lesson>"
                    + " | deckvoice <root> <lesson|all> <command...>");
        }
        if ("deckvoice".equals(args[0])) {
            deckVoice(Path.of(args[1]), args[2], List.of(Arrays.copyOfRange(args, 3, args.length)));
            return;
        }
        final Media media = new Media(Path.of(args[1]), args[2]);
        switch (args[0]) {
            case "record" -> new Recorder(Clock.systemUTC()).record(media);
            case "voice" -> voice(media, List.of(Arrays.copyOfRange(args, 3, args.length)));
            default -> assemble(media);
        }
    }

    static void deckVoice(final Path root, final String lesson, final List<String> command) {
        if (command.isEmpty()) {
            throw new IllegalArgumentException("give the voice command: a program that turns a text file into a WAV");
        }
        SlideDeck.lessons(root, lesson).forEach(media -> {
            DeckReader.speak(media, command);
            SlideDeck.write(media, false);
        });
    }

    static void voice(final Media media, final List<String> command) {
        if (command.isEmpty()) {
            throw new IllegalArgumentException("give the voice command: a program that turns a text file into a WAV");
        }
        final List<Scene> scenes = Recording.scenes(media);
        final Pronunciation pronunciation = DeckVoice.pronunciation(media);
        final List<Spoken> narration = Narration.paragraphs(media.file(".narration.md")).stream()
                .map(paragraph -> Spoken.of(paragraph, pronunciation)).toList();
        final Path clipsFolder = media.file(".voice");
        createFolder(clipsFolder);
        final List<WavFile> clips = new ArrayList<>();
        for (final Spoken scene : narration) {
            clips.add(VoiceClips.wav(media, command, scene));
        }
        final List<String> misfits = Voiceover.misfits(scenes, clips);
        if (!misfits.isEmpty()) {
            throw new IllegalStateException("The narration does not fit the video:\n  " + String.join("\n  ", misfits));
        }
        Voiceover.track(scenes, clips).write(media.file(".narration.wav"));
        final Slides slides = media.slides();
        final Voice voice = new Voice(media, command, pronunciation);
        speakSlides(voice, slides.intro(), "intro");
        speakSlides(voice, slides.closing(), "closing");
        log.info("{}: narration for {} scenes and {} slides written", LogSafe.lesson(media), clips.size(),
                slides.intro().size() + slides.closing().size());
    }

    static void assemble(final Media media) {
        final Path folder = media.folder();
        final String recording = String.valueOf(Zoom.zoomed(media).getFileName());
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error",
                "-i", recording, "-i", media.name(".narration.wav"),
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
        final String picture = burnCaptions() ? burned(media, cues) : media.name(".joined.mp4");
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error",
                "-i", picture, "-i", media.name(".vtt"), "-map", "0", "-map", "1", "-c", "copy",
                "-c:s", "mov_text", "-metadata:s:s:0", "language=eng", media.name(".mp4")), folder);
        ExternalCommand.run(List.of(Ffmpeg.program(), "-y", "-loglevel", "error",
                "-i", media.name(".joined.mp4"), "-vn", "-c:a", "aac", "-b:a", "128k", media.name(".narration.m4a")),
                folder);
        log.info("{}: finished video written: {} intro slides, the recording, {} closing slides, voice and captions",
                LogSafe.lesson(media), intro.size(), closing.size());
    }

    /** Captions are burned into the picture unless {@code lessonBurnCaptions} is false. */
    private static boolean burnCaptions() {
        return Boolean.parseBoolean(System.getProperty("video.burnCaptions", "true"));
    }

    /** The joined video with its captions burned in, as {@code <id>.captioned.mp4}; returns its file name. */
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

    /** The lesson, the voice command and how it should say awkward words. */
    private record Voice(Media media, List<String> command, Pronunciation pronunciation) {
    }

    /** Each slide's clip, from the lesson's store of spoken clips, kept as {@code <kind>-<n>.wav} for the splicer. */
    private static void speakSlides(final Voice voice, final List<Slide> slides, final String kind) {
        for (int i = 0; i < slides.size(); i++) {
            VoiceClips.wav(voice.media(), voice.command(), Spoken.of(slides.get(i).narration(), voice.pronunciation()))
                    .write(voice.media().file(".voice").resolve(kind + "-" + (i + 1) + ".wav"));
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
    static WavFile speak(final List<String> command, final String narration, final Path clip) {
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
