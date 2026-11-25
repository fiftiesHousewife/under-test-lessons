package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
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
 *   <li>{@code clips <root> <id>}: cuts each live-run scene out of the silent video into {@code <id>.deck-clips/},
 *   for the deck to play ({@link LiveRunClips});</li>
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
        checkUsage(args);
        if ("deckvoice".equals(args[0])) {
            deckVoice(Path.of(args[1]), args[2], List.of(Arrays.copyOfRange(args, 3, args.length)));
            return;
        }
        final Media media = new Media(Path.of(args[1]), args[2]);
        switch (args[0]) {
            case "record" -> new Recorder(Clock.systemUTC()).record(media);
            case "voice" -> voice(media, List.of(Arrays.copyOfRange(args, 3, args.length)));
            case "clips" -> LiveRunClips.cut(media);
            default -> FinishedVideo.assemble(media);
        }
    }

    private static void checkUsage(final String... args) {
        final List<String> commands = List.of("record", "voice", "assemble", "clips", "deckvoice");
        if (args.length < 3 || !commands.contains(args[0])) {
            throw new IllegalArgumentException("usage: LessonVideo record <root> <lesson>"
                    + " | voice <root> <lesson> <command...> | assemble <root> <lesson> | clips <root> <lesson>"
                    + " | deckvoice <root> <lesson|all> <command...>");
        }
    }

    static void deckVoice(final Path root, final String lesson, final List<String> command) {
        requireCommand(command);
        Media.lessons(root, lesson).forEach(media -> {
            DeckReader.speak(media, command);
            SlideDeck.write(media, false);
        });
    }

    private static void requireCommand(final List<String> command) {
        if (command.isEmpty()) {
            throw new IllegalArgumentException("give the voice command: a program that turns a text file into a WAV");
        }
    }

    static void voice(final Media media, final List<String> command) {
        requireCommand(command);
        final List<Scene> scenes = Recording.scenes(media);
        final Pronunciation pronunciation = DeckVoice.pronunciation(media);
        final List<Spoken> narration = Narration.paragraphs(media.file(".narration.md")).stream()
                .map(paragraph -> Spoken.of(paragraph, pronunciation)).toList();
        createFolder(media.file(".voice"));
        final Slides slides = media.slides();
        VoiceClips.readAhead(media, command, everything(narration, slides, pronunciation));
        final List<WavFile> clips = new ArrayList<>();
        for (final Spoken scene : narration) {
            clips.add(VoiceClips.wav(media, command, scene));
        }
        writeTrack(media, scenes, clips);
        final Voice voice = new Voice(media, command, pronunciation);
        speakSlides(voice, slides.intro(), "intro");
        speakSlides(voice, slides.closing(), "closing");
        log.info("{}: narration for {} scenes and {} slides written", LogSafe.lesson(media), clips.size(),
                slides.intro().size() + slides.closing().size());
    }

    /* Every piece the voice reads for the video: each scene's narration, then each slide's. */
    private static List<Spoken> everything(final List<Spoken> narration, final Slides slides,
            final Pronunciation pronunciation) {
        final List<Spoken> everything = new ArrayList<>(narration);
        Stream.concat(slides.intro().stream(), slides.closing().stream())
                .forEach(slide -> everything.add(Spoken.of(slide.narration(), pronunciation)));
        return everything;
    }

    /* The narration track: each scene's clip at its scene's start, if every clip fits its scene. */
    private static void writeTrack(final Media media, final List<Scene> scenes, final List<WavFile> clips) {
        final List<String> misfits = Voiceover.misfits(scenes, clips);
        if (!misfits.isEmpty()) {
            throw new IllegalStateException("The narration does not fit the video:\n  " + String.join("\n  ", misfits));
        }
        Voiceover.track(scenes, clips).write(media.file(".narration.wav"));
    }

    /* The lesson, the voice command and how it should say awkward words. */
    private record Voice(Media media, List<String> command, Pronunciation pronunciation) {
    }

    /* Each slide's clip, from the lesson's store of spoken clips, kept as {@code <kind>-<n>.wav} for the splicer. */
    private static void speakSlides(final Voice voice, final List<Slide> slides, final String kind) {
        for (int i = 0; i < slides.size(); i++) {
            VoiceClips.wav(voice.media(), voice.command(), Spoken.of(slides.get(i).narration(), voice.pronunciation()))
                    .write(voice.media().file(".voice").resolve(kind + "-" + (i + 1) + ".wav"));
        }
    }

    /* Speaks one piece of narration: each part between pause markers, joined with the pauses' silence. */
    static WavFile speak(final List<String> command, final String narration, final Path clip) {
        return Speech.readAll(command, List.of(Speech.of(narration, clip))).getFirst();
    }

    private static void createFolder(final Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
