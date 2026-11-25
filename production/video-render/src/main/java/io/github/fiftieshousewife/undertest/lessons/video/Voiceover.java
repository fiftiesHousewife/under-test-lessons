package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Places one spoken clip at the start of each scene to make a single narration track, after checking that every clip
 * fits its scene. A clip that runs longer than its scene would talk over the next one, so it is reported with how much
 * longer the scene's pauses must be. {@code BREATH_SECONDS} is left at the end of each scene after its narration, so
 * speech never runs into the next scene.
 */
public final class Voiceover {

    static final double BREATH_SECONDS = 1.0;

    private Voiceover() {
    }

    /** One message per scene whose narration does not fit, naming the scene and how many seconds to add. */
    public static List<String> misfits(final List<Scene> scenes, final List<WavFile> clips) {
        final List<String> problems = new ArrayList<>();
        for (int i = 0; i < scenes.size(); i++) {
            final Scene scene = scenes.get(i);
            final double length = scene.end() - scene.start();
            final double needed = clips.get(i).seconds() + BREATH_SECONDS;
            if (needed > length) {
                problems.add(String.format(Locale.ROOT,
                        "scene %d (%s): narration needs %.1f s but the scene shows for %.1f s; add %.0f s to its"
                                + " pauses",
                        i + 1, scene.title(), needed, length, Math.ceil(needed - length)));
            }
        }
        return problems;
    }

    /**
     * The narration track: silence, then each clip starting at its scene's start. Every clip must have the first clip's
     * rate and channels, as a person's recording might not, or it would play at the wrong speed.
     */
    public static WavFile track(final List<Scene> scenes, final List<WavFile> clips) {
        final WavFile format = clips.getFirst();
        requireOneFormat(clips);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final int bytesPerFrame = format.channels() * 2;
        for (int i = 0; i < scenes.size(); i++) {
            final int startByte = format.frames(scenes.get(i).start()) * bytesPerFrame;
            out.writeBytes(new byte[Math.max(0, startByte - out.size())]);
            out.writeBytes(clips.get(i).samples());
        }
        final int endByte = format.frames(scenes.getLast().end()) * bytesPerFrame;
        out.writeBytes(new byte[Math.max(0, endByte - out.size())]);
        return new WavFile(format.sampleRate(), format.channels(), out.toByteArray());
    }

    private static void requireOneFormat(final List<WavFile> clips) {
        final WavFile format = clips.getFirst();
        for (int i = 1; i < clips.size(); i++) {
            if (clips.get(i).sampleRate() != format.sampleRate() || clips.get(i).channels() != format.channels()) {
                throw new IllegalStateException(String.format(Locale.ROOT,
                        "scene %d's clip is %d Hz with %d channel(s), but scene 1's is %d Hz with %d; record every"
                                + " clip the same way",
                        i + 1, clips.get(i).sampleRate(), clips.get(i).channels(), format.sampleRate(),
                        format.channels()));
            }
        }
    }
}
