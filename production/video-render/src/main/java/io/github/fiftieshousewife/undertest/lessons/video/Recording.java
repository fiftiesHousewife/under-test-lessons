package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.List;

/**
 * A lesson's scenes and zooms timed to its recorded video: the times the script plans, stretched to the recording's
 * real length, measured with ffprobe. Only the machine that makes the videos has a recording to measure.
 */
final class Recording {

    private Recording() {
    }

    /** The scenes, stretched to the recorded silent video's real length when it exists. */
    static List<Scene> scenes(final Media media) {
        final List<Scene> planned = media.plannedScenes();
        final double factor = stretch(media, planned.getLast().end());
        return planned.stream().map(s -> new Scene(s.title(), s.start() * factor, s.end() * factor)).toList();
    }

    /** The zooms on key code, stretched as the scenes are. */
    static List<ZoomTiming.Span> zooms(final Media media) {
        final List<ZoomTiming.Span> planned = media.plannedZooms();
        if (planned.isEmpty()) {
            return planned;
        }
        final double factor = stretch(media, media.plannedScenes().getLast().end());
        return planned.stream().map(span -> span.scaled(factor)).toList();
    }

    /** How much longer (or shorter) the recording came out than the script planned. */
    private static double stretch(final Media media, final double plannedLength) {
        return Ffmpeg.videoSeconds(media.file(".silent.mp4")).orElse(plannedLength) / plannedLength;
    }
}
